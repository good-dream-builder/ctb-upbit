package com.zzup.ctbupbit.trx;

import com.zzup.ctbupbit.accounting.AccountBook;
import com.zzup.ctbupbit.accounting.AccountBookRepository;
import com.zzup.ctbupbit.accounting.AccountingService;
import com.zzup.ctbupbit.common.DealType;
import com.zzup.ctbupbit.policy.PolicyContainer;
import com.zzup.ctbupbit.provider.UpbitRestApiCaller;
import com.zzup.ctbupbit.provider.dto.*;
import com.zzup.ctbupbit.operation.urgencyop.UrgencyOp;
import com.zzup.ctbupbit.operation.urgencyop.UrgencyOpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class SellTrx implements BaseTrx {
    public Logger logger = LoggerFactory.getLogger(SellTrx.class);

    // [ Constants ]
    private final Double UPBIT_LIMIT_UNIT_PRICE = 5001.0;
    private final Double FEE_RATE = 0.0005;

    // 매수, 매도 수수료 (0.05%) 각각의 2배.0.001 0.1%
//    private final Double LIMIT_RATE = 1.001;
    // 0.3% 이상이면 매도 하도록 한다.
    private final Double LIMIT_RATE = 1.003;

    // [ Injection ]
    private UpbitRestApiCaller api;
    private PolicyContainer policy;
    private AccountBookRepository accountBookRepository;
    private AccountingService accountingService;
    private UrgencyOpService urgencyOpService;

    // [ Members ]
    private String markets;
    private Map<String, Account> accountMap;

    SellTrx(UpbitRestApiCaller api,
            PolicyContainer policy,
            AccountBookRepository accountBookRepository,
            AccountingService accountingService,
            UrgencyOpService urgencyOpService) {
        this.api = api;
        this.policy = policy;
        this.accountBookRepository = accountBookRepository;
        this.accountingService = accountingService;
        this.urgencyOpService = urgencyOpService;
    }

    @Override
    public void order() {
        // Step1. 내 계좌 정보를 가져온다.
        List<Account> accountList = api.getAccountList();
        accountMap = new HashMap<>();
        Account krwAccount = null;
        for (Account el : accountList) {
            final String currency = el.getCurrency();
            if (currency.equals("KRW")) {
                krwAccount = el;
            } else if (currency.equals("NPXS") || currency.contains("USDT") || currency.equals("XEC")) {
                // TODO 제외대상 추가 영역
            } else if (Double.parseDouble(el.getAvg_buy_price()) > 0) {
                UrgencyOp urgencyOp = urgencyOpService.getUrgencyOp();
                if (currency.equals(urgencyOp.getTarget()) == false) {
                    String market = "KRW-" + currency;
                    accountMap.put(market, el);
                }
            }
        }
//        logger.debug("SellTrx::order::accountMap = " + accountMap);

        // Step2. 전체 종목 중 Sell score가 높은 순서로 정리한다.

        // Step4. 시장 현재 호가를 가져온다.
        List<OrderBook> orderBookList = getOrderBookList();
        if (orderBookList == null) return;

        // Step5. 주문한다.
        for (OrderBook orderBook : orderBookList) {

            final String market = orderBook.getMarket();
//            logger.debug("SellTrx::order::market = " + market);

            OrderBookUnit orderBookUnit = orderBook.getOrderbook_units().get(0);
            final Double bidPrice = orderBookUnit.getBid_price(); // 시장 매수 호가
            final Double askPrice = orderBookUnit.getAsk_price(); // 시장 매도 호가

            Account myAccount = accountMap.get(market);
            if (myAccount == null) {
                continue;
            }
            Double myPrice = Double.parseDouble(myAccount.getAvg_buy_price());
            Double myAmount = Double.parseDouble(myAccount.getBalance());
//            logger.debug("SellTrx::order::myPrice = " + myPrice);

//            AccountBook lastAccountBook = accountingService.getLastAccountBookByCoin(market);
//            Double accFee = lastAccountBook.getAccFee();
//            logger.debug("SellTrx::order::accFee = " + accFee);

//            logger.debug("SellTrx::order::myAccount = " + myAccount);
//            logger.debug("SellTrx::order::bidPrice = " + bidPrice);
//            logger.debug("SellTrx::order::askPrice = " + askPrice);

            Double myLimitPrice = myPrice * LIMIT_RATE;
//            logger.debug("SellTrx::order::myPrice * LIMIT_RATE = " + myLimitPrice);

            Double fee = (myPrice * myAmount) * FEE_RATE;
//            logger.debug("SellTrx::order::fee = " + fee);

            // 수수료로 계샨 해보려고 했으나 이상함.
//            Double myLimitPrice = myPrice + ((fee + accFee) / myAmount);
//            logger.debug("SellTrx::order::myLimitPrice = " + myLimitPrice);

            // 사겠다는 가격이 내 가격 보다 크면 판다
            if (myLimitPrice < bidPrice) {

                if ((myAmount * bidPrice) < UPBIT_LIMIT_UNIT_PRICE) {
                    logger.debug(market + " : 5000원 보다 적어서 팔 수가 없습니다. = " + (myAmount * bidPrice));
                    continue;
                }


                final Double unitAmount = Math.ceil((UPBIT_LIMIT_UNIT_PRICE / bidPrice) * 100) / 100.0;
                logger.debug("{} 코인의 총 {} 개 중 {} 개를 매도합니다." + market, myAmount, unitAmount);

                // 1) 주문서 작성
                OrderReq orderReq = new OrderReq();
                orderReq.setMarket(market);
                orderReq.setSide(OrderSide.SELL);

                // 최소 금액으로 팔았을 때 5,000원 미만이 되지 않기 위해서.
                if (((myAmount - unitAmount) * bidPrice) < UPBIT_LIMIT_UNIT_PRICE) {
                    orderReq.setVolume(String.valueOf(myAmount));
                } else {
                    orderReq.setVolume(String.valueOf(unitAmount));
                }
                orderReq.setPrice(String.valueOf(bidPrice));
//                logger.debug("SellTrx::order::orderReq = " + orderReq);


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
                accountBook.setDeal(DealType.SELL);
                accountBook.setMyPrice(myPrice);
                accountBook.setMarketPrice(Double.parseDouble(orderResp.getPrice()));
                accountBook.setTrxAmount(Double.parseDouble(orderResp.getVolume()));
                accountBook.setDealUuid(orderResp.getUuid());
                accountBook.setPolicyResult("profit");

                accountBook.setFee(fee);
//                accountBook.setAccFee(0.0);

                accountBookRepository.save(accountBook);

                logger.debug("[수익 발생 : " + accountBook.getCoin() + "] = " + accountBook.getProfit());
            }
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
