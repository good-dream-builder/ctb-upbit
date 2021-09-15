package com.zzup.ctbupbit.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzup.ctbupbit.provider.dto.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@Component
public class UpbitRestApiCaller {
    public Logger logger = LoggerFactory.getLogger(UpbitRestApiCaller.class);

    @Autowired
    CallLimitContainer callLimit;

    private final String API_HOST = "https://api.upbit.com";
    private final String ACCESS_KEY = "hxU2pxhbRfdGCHua9G1FAYhmQNnm5luZxuKGpENJ";
    private final String SECRETE_KEY = "Kqwm4qKKgtW5xqATGj7SwYQtmKX5sxKlHvxrchwJ";

    private Algorithm algorithm;

    public UpbitRestApiCaller() {
        algorithm = Algorithm.HMAC256(SECRETE_KEY);
    }

    /**
     * [ 분(Minute) 캔들 ]
     * group : candles
     */
    public List<CandleMinute> getMinuteCandleList(String marketCoinName, Integer minUnit, Integer count) throws NoSuchAlgorithmException, IOException {
//        logger.debug("getMinuteCandleList");

        if (!callLimit.canICallApi("candles")) {
            callLimit.waitForAvailableCallApi("candles");
        }

        List<CandleMinute> candleMinuteList = null;
        String uri = API_HOST + "/v1/candles/minutes/" + String.valueOf(minUnit) + "?market=" + marketCoinName + "&count=" + String.valueOf(count);
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(uri);

            HttpResponse response = client.execute(request);
            callLimit.updateCallLimit(response);

            HttpEntity entity = response.getEntity();
            String jsonStrResp = EntityUtils.toString(entity, "UTF-8");

            if (jsonStrResp.contains("error") == false) {
                ObjectMapper objectMapper = new ObjectMapper();
                candleMinuteList = Arrays.asList(objectMapper.readValue(jsonStrResp, CandleMinute[].class));
            } else {
                logger.error(jsonStrResp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return candleMinuteList;
    }


    /**
     * [ 주문하기 ]
     * group : order
     */
    public OrderResp order(OrderReq orderReq) throws NoSuchAlgorithmException, IOException {
//        logger.debug("order");

        if (!callLimit.canICallApi("order")) {
            callLimit.waitForAvailableCallApi("order");
        }

        OrderResp orderResp = null;

        HashMap<String, String> params = new HashMap<>();
        params.put("market", orderReq.getMarket());
        params.put("side", orderReq.getSide().getType());
        params.put("volume", orderReq.getVolume());
        params.put("price", orderReq.getPrice());
        params.put("ord_type", "limit");


        ArrayList<String> queryElements = new ArrayList<>();
        for (Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }

        String queryString = String.join("&", queryElements.toArray(new String[0]));

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes("UTF-8"));

        String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

        int retryCount = 0;
        while (retryCount < 3) {
            String jwtToken = JWT.create()
                    .withClaim("access_key", ACCESS_KEY)
                    .withClaim("nonce", UUID.randomUUID().toString())
                    .withClaim("query_hash", queryHash)
                    .withClaim("query_hash_alg", "SHA512")
                    .sign(algorithm);

            String authenticationToken = "Bearer " + jwtToken;


            HttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(API_HOST + "/v1/orders");
            request.setHeader("Content-Type", "application/json");
            request.addHeader("Authorization", authenticationToken);
            request.setEntity(new StringEntity(new JSONObject(params).toString()));


            HttpResponse response = client.execute(request);
            callLimit.updateCallLimit(response);

            HttpEntity entity = response.getEntity();

            String jsonStrResp = EntityUtils.toString(entity, "UTF-8");
            if (jsonStrResp.contains("error") == false && jsonStrResp.contains("Too") == false) {
                ObjectMapper objectMapper = new ObjectMapper();
                orderResp = objectMapper.readValue(jsonStrResp, OrderResp.class);
                retryCount = 3;
            } else {
                logger.error(orderReq.getMarket() +" : "+ jsonStrResp);
                retryCount += 1;

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return orderResp;
    }

    /**
     * [ 현재가 정보 ]
     * group : ticker
     * 요청 당시 종목의 스냅샷을 반환한다.
     */
    public List<Ticker> getTickerList(final String marketsParam) {
//        logger.debug("getTickerList");

        if (!callLimit.canICallApi("ticker")) {
            callLimit.waitForAvailableCallApi("ticker");
        }

        List<Ticker> tickerList = null;

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(API_HOST + "/v1/ticker?markets=" + marketsParam);

            HttpResponse response = client.execute(request);
            callLimit.updateCallLimit(response);

            HttpEntity entity = response.getEntity();

            String jsonStrResp = EntityUtils.toString(entity, "UTF-8");

            if (jsonStrResp.contains("error") == false) {
                ObjectMapper objectMapper = new ObjectMapper();
                tickerList = Arrays.asList(objectMapper.readValue(jsonStrResp, Ticker[].class));
            } else {
                logger.error(jsonStrResp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return tickerList;
    }


    /**
     * [ 시세 호가 정보 ]
     * group : orderbook
     * 호가 정보 조회
     */
    public List<OrderBook> showOrderBook(final String marketsParam) {
//        logger.debug("showOrderBook");

        if (!callLimit.canICallApi("orderbook")) {
            callLimit.waitForAvailableCallApi("orderbook");
        }

        List<OrderBook> orderBookList = null;

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(API_HOST + "/v1/orderbook?markets=" + marketsParam);

            HttpResponse response = client.execute(request);
            callLimit.updateCallLimit(response);

            HttpEntity entity = response.getEntity();
            String jsonStrResp = EntityUtils.toString(entity, "UTF-8");

            if (jsonStrResp.contains("error") == false) {
                ObjectMapper objectMapper = new ObjectMapper();
                orderBookList = Arrays.asList(objectMapper.readValue(jsonStrResp, OrderBook[].class));
            } else {
                logger.error(jsonStrResp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return orderBookList;
    }

    /**
     * 마켓별 주문 가능 정보를 확인한다.
     * group : default
     */
    public OrderChance getOrdersChance(final String marketCoinName) throws NoSuchAlgorithmException, UnsupportedEncodingException {
//        logger.debug("getOrdersChance");

        if (!callLimit.canICallApi("default")) {
            callLimit.waitForAvailableCallApi("default");
        }

        OrderChance orderChance = null;

        HashMap<String, String> params = new HashMap<>();
        params.put("market", marketCoinName);

        ArrayList<String> queryElements = new ArrayList<>();
        for (Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }

        String queryString = String.join("&", queryElements.toArray(new String[0]));

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes("UTF-8"));

        String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

        String jwtToken = JWT.create()
                .withClaim("access_key", ACCESS_KEY)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(API_HOST + "/v1/orders/chance?" + queryString);
            request.setHeader("Content-Type", "application/json");
            request.addHeader("Authorization", authenticationToken);

            HttpResponse response = client.execute(request);
            callLimit.updateCallLimit(response);

            HttpEntity entity = response.getEntity();

            String jsonStrResp = EntityUtils.toString(entity, "UTF-8");

            if (jsonStrResp.contains("error") == false) {
                ObjectMapper objectMapper = new ObjectMapper();
                orderChance = objectMapper.readValue(jsonStrResp, OrderChance.class);
            } else {
                logger.error(jsonStrResp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return orderChance;
    }

    /**
     * 내가 보유한 자산 리스트를 보여줍니다.
     * group : default
     */
    public List<Account> getAccountList() {
//        logger.debug("getAccountList");

        if (!callLimit.canICallApi("default")) {
            callLimit.waitForAvailableCallApi("default");
        }

        List<Account> accountsList = null;

        String resp = "";
        String jwtToken = JWT.create()
                .withClaim("access_key", ACCESS_KEY)
                .withClaim("nonce", UUID.randomUUID().toString())
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(API_HOST + "/v1/accounts");
            request.setHeader("Content-Type", "application/json");
            request.addHeader("Authorization", authenticationToken);

            HttpResponse response = client.execute(request);
            callLimit.updateCallLimit(response);

            HttpEntity entity = response.getEntity();

            String jsonStrResp = EntityUtils.toString(entity, "UTF-8");

            if (jsonStrResp.contains("error") == false) {
                ObjectMapper objectMapper = new ObjectMapper();
                accountsList = Arrays.asList(objectMapper.readValue(jsonStrResp, Account[].class));
            } else {
                logger.error(jsonStrResp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return accountsList;
    }
}
