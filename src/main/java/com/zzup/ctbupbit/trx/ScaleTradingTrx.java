package com.zzup.ctbupbit.trx;

import com.zzup.ctbupbit.accounting.AccountBook;
import com.zzup.ctbupbit.accounting.AccountBookRepository;
import com.zzup.ctbupbit.accounting.AccountingService;
import com.zzup.ctbupbit.common.DealType;
import com.zzup.ctbupbit.provider.UpbitRestApiCaller;
import com.zzup.ctbupbit.provider.dto.*;
import com.zzup.ctbupbit.operation.serviceop.ServiceOpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ScaleTradingTrx implements BaseTrx {
    public Logger logger = LoggerFactory.getLogger(ScaleTradingTrx.class);

    // [ Constants ]
    private final Double FEE_RATE = 0.0005;
    //    private final Double UNIT_PRICE = 5001.0;
    private final Double UPBIT_LIMIT_UNIT_PRICE = 5001.0;

    private final Double TARGET_RATE = -0.05; // -5.0%

    // [ Injection ]
    private UpbitRestApiCaller api;
    private ServiceOpService serviceOpService;
    private AccountingService accountingService;
    private AccountBookRepository accountBookRepository;

    // [ Members ]
    private String markets;
    private Map<String, Account> accountMap;

    ScaleTradingTrx(UpbitRestApiCaller api,
                    ServiceOpService serviceOpService,
                    AccountingService accountingService,
                    AccountBookRepository accountBookRepository) {
        this.api = api;
        this.serviceOpService = serviceOpService;
        this.accountingService = accountingService;
        this.accountBookRepository = accountBookRepository;
    }

    @Override
    public void order() {
        Double myMoney = 0.0;
        List<Account> accountList = api.getAccountList();
        accountMap = new HashMap<>();

        for (Account el : accountList) {
            final String currency = el.getCurrency();
            if (el.getCurrency().equals("KRW")) {
                myMoney = Double.parseDouble(el.getBalance());
            } else if (currency.equals("NPXS")) {
                // TODO 제외대상 추가 영역
            } else if (
              Double.parseDouble(el.getAvg_buy_price()) > 0 &&
              currency.equals("BTC") || currency.equals("ETH") || currency.equals("DOT")
            ) {
                String market = "KRW-" + currency;
                accountMap.put(market, el);
            }
        }

        List<OrderBook> orderBookList = getOrderBookList();
        if (orderBookList == null) return;

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
            Double diffPrice = askPrice - myPrice;
            Double diffRate = diffPrice / myPrice;
            if (diffRate > TARGET_RATE) {
                logger.debug(market + " : 물타기 비율에 도달하지 않았습니다. = " + (diffRate * 100));
                continue;
            }

            final Double UNIT_PRICE = serviceOpService.getBuyUnitPrice();
            if (myMoney < UNIT_PRICE) {
                logger.debug("돈이 없어 물타지 않습니다. = " + myMoney);
                continue;
            }

            Double amount = UNIT_PRICE / askPrice;

            // 1) 주문서 작성
            OrderReq orderReq = new OrderReq();
            orderReq.setMarket(market);
            orderReq.setSide(OrderSide.BUY);
            orderReq.setVolume(String.valueOf(amount));
            orderReq.setPrice(String.valueOf(askPrice));
            logger.debug("ScaleTradingTrx::order::orderReq = " + orderReq);

            // 2) 주문하기
            OrderResp orderResp = null;
            try {
                orderResp = api.order(orderReq);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            myMoney -= UNIT_PRICE;

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
            accountBook.setDeal(DealType.BUY);
            accountBook.setMyPrice(askPrice);
            accountBook.setMarketPrice(Double.parseDouble(orderResp.getPrice()));
            accountBook.setTrxAmount(Double.parseDouble(orderResp.getVolume()));
            accountBook.setDealUuid(orderResp.getUuid());

            final String policyResult = "scale trading";
            accountBook.setPolicyResult(policyResult);

            Double fee = UNIT_PRICE * FEE_RATE;
            accountBook.setFee(fee);

            accountBookRepository.save(accountBook);
        }
    }

    private List<OrderBook> getOrderBookList() {
        StringBuffer stringBuffer = new StringBuffer();

        int i = 0;
        for (Account el : accountMap.values()) {
            final String marketCoinName = "KRW-" + el.getCurrency();
            stringBuffer.append(marketCoinName);
            if (i != (accountMap.values().size() - 1)) {
                stringBuffer.append(",");
            }
            i++;
        }
        markets = stringBuffer.toString();
//        logger.debug("SellTrx::order::markets = " + markets);

        List<OrderBook> orderBookList = null;
        if (markets.length() > 1) {
            orderBookList = api.showOrderBook(markets);
//            logger.debug("SellTrx::order::orderBookList = " + orderBookList);
        }

        return orderBookList;
    }

}

