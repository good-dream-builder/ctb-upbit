package com.zzup.ctbupbit.trx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzup.ctbupbit.accounting.AccountBook;
import com.zzup.ctbupbit.accounting.AccountBookRepository;
import com.zzup.ctbupbit.common.DealType;
import com.zzup.ctbupbit.policy.CoinPolicyResult;
import com.zzup.ctbupbit.policy.PolicyContainer;
import com.zzup.ctbupbit.provider.UpbitRestApiCaller;
import com.zzup.ctbupbit.provider.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class StopLossTrxOld implements BaseTrx {
    public Logger logger = LoggerFactory.getLogger(StopLossTrxOld.class);

    // [ Constants ]
    private final Double UPBIT_LIMIT_UNIT_PRICE = 5001.0;
    private final Double STOP_LOSS_RATE = 0.03; // -3%

    // [ Injection ]
    private UpbitRestApiCaller api;
    private PolicyContainer policy;
    private AccountBookRepository accountBookRepository;

    // [ Members ]
    private List<CoinPolicyResult> coinPolicyResultList;
    private String markets;
    private Map<String, Account> accountMap;

    StopLossTrxOld(UpbitRestApiCaller api,
                   PolicyContainer policy,
                   AccountBookRepository accountBookRepository) {
        this.api = api;
        this.policy = policy;
        this.accountBookRepository = accountBookRepository;
    }

    @Override
    public void order() {
        // Step1. 내 계좌 정보를 가져온다.
        List<Account> accountList = api.getAccountList();
        accountMap = new HashMap<>();
        Account krwAccount = null;
        for (Account el : accountList) {
            if (el.getCurrency().equals("KRW")) {
                krwAccount = el;
            } else if (el.getCurrency().equals("VTHO")) {
                // 예외
            } else {
                String market = "KRW-" + el.getCurrency();
                accountMap.put(market, el);
            }
        }
//        logger.debug("StopLossTrx::order::accountMap = " + accountMap);

        // Step2. 전체 종목 중 Stop-Loos score가 높은 순서로 정리한다.
        Map<String, CoinPolicyResult> coinPolicyResultMap = policy.getCoinPolicyResultMap();
        coinPolicyResultList = new ArrayList<>(coinPolicyResultMap.values());
//        logger.debug("StopLossTrx::order::coinPolicyResultList(original) = " + coinPolicyResultList);

        // 편입 해제 된(CoinType에서 제거 된) 코인들을 등록한다.
        for (Account el : accountMap.values()) {
            String marketCoinName = "KRW-" + el.getCurrency();
            if (coinPolicyResultMap.get(marketCoinName) == null) {
                logger.debug(marketCoinName + " : 편입 해제 된 코인을 손절 리스트에 등록합니다.");
                CoinPolicyResult temp = new CoinPolicyResult();
                temp.setMarketCoinName(marketCoinName);
                temp.setStopLossScore(1000000);
                coinPolicyResultList.add(temp);
            }
        }

        sortByScore();

        // Step3. 내 계좌에의 목록이 상위권(50개)에 있는 경우 포함.
        makeTarget(25);

        // Step4. 시장 현재 호가를 가져온다.
        List<OrderBook> orderBookList = getOrderBookList();
        if (orderBookList == null) return;

        // Step5. 주문한다.
        for (OrderBook orderBook : orderBookList) {
            final String market = orderBook.getMarket();
            OrderBookUnit orderBookUnit = orderBook.getOrderbook_units().get(0);
            final Double bidPrice = orderBookUnit.getBid_price(); // 시장 매수 호가
            final Double askPrice = orderBookUnit.getAsk_price(); // 시장 매도 호가

            Account myAccount = accountMap.get(market);
            if (myAccount == null) {
                continue;
            }

            Double myPrice = Double.parseDouble(myAccount.getAvg_buy_price());
            Double myAmount = Double.parseDouble(myAccount.getBalance());

//            logger.debug("StopLossTrx::order::myAccount = " + myAccount);
//            logger.debug("StopLossTrx::order::bidPrice = " + bidPrice);
//            logger.debug("StopLossTrx::order::askPrice = " + askPrice);

            Double availSellPrice = myAmount * bidPrice;
            if (availSellPrice < UPBIT_LIMIT_UNIT_PRICE) {
//                logger.debug(market + " : 5000원 보다 적어서 팔 수가 없습니다. = " + availSellPrice);
                continue;
            }

            Double diffPrice = Math.abs(bidPrice - myPrice);
            Double diffRate = diffPrice / myPrice;

//            logger.debug("StopLossTrx::order::diffPrice = " + diffPrice);
//            logger.debug("StopLossTrx::order::diffRate = " + diffRate);

            if (diffRate < STOP_LOSS_RATE) {
//                logger.debug(market + " : 손절 비율가에 도달하지 않았습니다. = " + diffRate);
                continue;
            }

            // 1) 주문서 작성
            OrderReq orderReq = new OrderReq();
            orderReq.setMarket(market);
            orderReq.setSide(OrderSide.SELL);
            orderReq.setVolume(String.valueOf(myAmount));
            orderReq.setPrice(String.valueOf(bidPrice));
//            logger.debug("StopLossTrx::order::orderReq = " + orderReq);


            // 2) 주문하기
            OrderResp orderResp = null;
            try {
                orderResp = api.order(orderReq);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 3) 주문내역 저장
            // "2021-02-23T03:02:13+09:00"
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.KOREA);
            format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            AccountBook accountBook = new AccountBook();
            try {
                accountBook.setDateTime(format.parse(orderResp.getCreated_at()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            accountBook.setCoin(market);
            accountBook.setDeal(DealType.STOP_LOSS);
            accountBook.setMyPrice(myPrice);
            accountBook.setMarketPrice(Double.parseDouble(orderResp.getPrice()));
            accountBook.setTrxAmount(Double.parseDouble(orderResp.getVolume()));
            accountBook.setDealUuid(orderResp.getUuid());

            ObjectMapper objectMapper = new ObjectMapper();
            String policyResult = "";
            try {
                policyResult = objectMapper.writeValueAsString(coinPolicyResultMap.get(market));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            accountBook.setPolicyResult(policyResult);
            accountBookRepository.save(accountBook);



        }
    }

    private List<OrderBook> getOrderBookList() {
        StringBuffer stringBuffer = new StringBuffer();

        int i = 0;
        for (CoinPolicyResult el : coinPolicyResultList) {
            final String marketCoinName = el.getMarketCoinName();
            stringBuffer.append(marketCoinName);
            if (i != (coinPolicyResultList.size() - 1)) {
                stringBuffer.append(",");
            }
            i++;
        }
        markets = stringBuffer.toString();
//        logger.debug("StopLossTrx::order::markets = " + markets);

        List<OrderBook> orderBookList = null;
        if (markets.length() > 1) {
            orderBookList = api.showOrderBook(markets);
//            logger.debug("StopLossTrx::order::orderBookList = " + orderBookList);
        }

        return orderBookList;
    }

    private void makeTarget(int count) {
        coinPolicyResultList = coinPolicyResultList.subList(0, count);
        List<CoinPolicyResult> targetCoinPolicyResultList = new ArrayList<>();
        for (CoinPolicyResult el : coinPolicyResultList) {
//            String currency = el.getMarketCoinName().split("-")[1];
            String currency = el.getMarketCoinName();
//            logger.debug("StopLossTrx::order::makeTarget::currency = " + currency);

            Account account = accountMap.get(currency);
            if (account != null) {
//                logger.debug("StopLossTrx::makeTarget::account = " + account);

                // 음수 경합은 대상이 아니다.
                if(el.getStopLossScore() > 0) {
                    targetCoinPolicyResultList.add(el);
                }
            }
        }

        coinPolicyResultList = targetCoinPolicyResultList;
//        logger.debug("StopLossTrx::order::coinPolicyResultList(subList) = " + coinPolicyResultList);
    }

    private void sortByScore() {
        this.coinPolicyResultList.sort(new Comparator<CoinPolicyResult>() {
            @Override
            public int compare(CoinPolicyResult o1, CoinPolicyResult o2) {
                if (o1.getStopLossScore() < o2.getStopLossScore()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
//        logger.debug("StopLossTrx::sortByScore::coinPolicyResultList = " + coinPolicyResultList);
    }
}
