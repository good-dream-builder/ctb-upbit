package com.zzup.ctbupbit.trx;

import com.zzup.ctbupbit.accounting.AccountBook;
import com.zzup.ctbupbit.accounting.AccountBookRepository;
import com.zzup.ctbupbit.accounting.AccountingService;
import com.zzup.ctbupbit.common.DealType;
import com.zzup.ctbupbit.policy.CoinPolicyResult;
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
public class StopLossTrx implements BaseTrx {
    public Logger logger = LoggerFactory.getLogger(StopLossTrx.class);

    // [ Constants ]
//    private final Double UNIT_PRICE = 5001.0;
    private final Double UPBIT_LIMIT_UNIT_PRICE = 5001.0;

    private final Double STOP_LOSS_RATE_MINUS_1_5 = -0.015; // -1.5%
    private final Double STOP_LOSS_RATE_MINUS_2_0 = -0.020; // -2.0%
    private final Double STOP_LOSS_RATE_MINUS_2_5 = -0.025; // -2.5%
    private final Double STOP_LOSS_RATE_MINUS_3_0 = -0.030; // -3.0%
    private final Double STOP_LOSS_RATE_MINUS_3_5 = -0.035; // -3.5%
    private final Double STOP_LOSS_RATE_MINUS_4_0 = -0.040; // -4.0%
    private final Double STOP_LOSS_RATE_MINUS_4_5 = -0.045; // -4.5%
    private final Double STOP_LOSS_RATE_MINUS_5_0 = -0.050; // -5.0%
    private final Double STOP_LOSS_RATE_MINUS_5_5 = -0.055; // -5.5%

    private final Double STOP_LOSS_RATE = -0.09; // -9.0%

    // [ Injection ]
    private UpbitRestApiCaller api;
    private ServiceOpService serviceOpService;
    private AccountingService accountingService;
    private AccountBookRepository accountBookRepository;

    // [ Members ]
    private List<CoinPolicyResult> coinPolicyResultList;
    private Map<String, Account> accountMap;

    StopLossTrx(UpbitRestApiCaller api,
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
        // ?????? ????????? ????????????.
        Calendar tomorrowDate = Calendar.getInstance();
        tomorrowDate.add(Calendar.DAY_OF_YEAR, 1);
        String tomorrow = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(tomorrowDate.getTime());

        Calendar todayDate = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(todayDate.getTime());

        Double earnedMoney = 0.0;
        try {
            earnedMoney = accountingService.getProfit(today, tomorrow);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<Account> accountList = api.getAccountList();

        for (Account el : accountList) {
            final String currency = el.getCurrency();
            if (currency.equals("KRW") ||
                currency.equals("BTC") ||
                currency.equals("ETH") ||
                currency.equals("DOT")
            ) {
                // TODO KRW, BTC, ETH, DOT ??? ?????? ??????
            } else if (Double.parseDouble(el.getAvg_buy_price()) > 0) {
                final String market = "KRW-" + el.getCurrency();
                List<OrderBook> orderBookList = api.showOrderBook(market);
//                logger.debug("StopLossTrx::order::orderBookList = " + orderBookList);
                if (orderBookList == null) return;

                Double myPrice = Double.parseDouble(el.getAvg_buy_price());
                Double myAmount = Double.parseDouble(el.getBalance());

                for (OrderBook orderBook : orderBookList) {
                    OrderBookUnit orderBookUnit = orderBook.getOrderbook_units().get(0);
                    final Double bidPrice = orderBookUnit.getBid_price(); // ?????? ?????? ??????
                    final Double askPrice = orderBookUnit.getAsk_price(); // ?????? ?????? ??????
//                    logger.debug("StopLossTrx::order::market = " + market);
//                    logger.debug("StopLossTrx::order::bidPrice = " + bidPrice);
//                    logger.debug("StopLossTrx::order::askPrice = " + askPrice);

                    Double diffPrice = bidPrice - myPrice;
                    Double diffRate = diffPrice / myPrice;
//                    logger.debug("StopLossTrx::order::diffPrice = " + diffPrice);
//                    logger.debug("StopLossTrx::order::diffRate = " + diffRate);

                    /**
                     * (??????) -15.0% ???????????? ???????????? ?????????.
                     */
                    if (diffRate > STOP_LOSS_RATE) {
                        logger.debug(market + " : ?????? ???????????? ???????????? ???????????????. = " + diffRate);
                        continue;
                    }

                    final Double UNIT_PRICE = serviceOpService.getStopLossUnitPrice();
                    Double amount = UNIT_PRICE / bidPrice;

                    if ((bidPrice * myAmount) < UNIT_PRICE) {
                        // ?????? ?????? ???????????? UNIT_PRICE ?????? ?????????, ????????? ??? ????????? ????????????.
                        logger.debug(market + " : ?????? ?????? ???????????? ?????? ?????? ?????? ?????? ?????? ????????? ??? ????????????. = " + (bidPrice * myAmount));
                        continue;
                    } else if ((bidPrice * myAmount) < (UNIT_PRICE * 2)) {
                        // UNIT_PRICE * 2 ?????? ?????? ?????? ???????????? ?????????, ???????????? ??? ?????? ???????????? ????????? ??? ??????.
                        amount = myAmount;
                        logger.debug(market + " : ????????? ??? ?????????. = " + (bidPrice * amount));
                    }

                    /**
                     * (??????) ????????? ?????????, ???????????? ?????????.
                     */
//                    Double lossMoney = bidPrice * amount;
                    Double lossMoney = diffPrice * amount;
                    Double leftMoney = earnedMoney + lossMoney;
                    if (leftMoney < 0) {
                        logger.debug(market + " : ????????? ????????? ?????? ?????? ????????????. ?????? ?????? = " + earnedMoney + ", ????????? = " + (lossMoney));
                        continue;
                    } else {
                        earnedMoney = leftMoney;
                        logger.debug(market + " : ?????? ??? ?????? ?????? = " + earnedMoney + ", ????????? = " + (lossMoney));
                    }

                    // 1) ????????? ??????
                    OrderReq orderReq = new OrderReq();
                    orderReq.setMarket(market);
                    orderReq.setSide(OrderSide.SELL);
                    orderReq.setVolume(String.valueOf(amount));
                    orderReq.setPrice(String.valueOf(bidPrice));
//                    logger.debug("StopLossTrx::order::orderReq = " + orderReq);


                    // 2) ????????????
                    OrderResp orderResp = null;
                    try {
                        orderResp = api.order(orderReq);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 3) ???????????? ??????
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
                    accountBook.setFee(UNIT_PRICE * 0.0005);
                    accountBook.setMyPrice(myPrice);
                    accountBook.setMarketPrice(Double.parseDouble(orderResp.getPrice()));
                    accountBook.setTrxAmount(Double.parseDouble(orderResp.getVolume()));
                    accountBook.setDealUuid(orderResp.getUuid());

                    final String policyResult = "stop loss";
                    accountBook.setPolicyResult(policyResult);
                    accountBookRepository.save(accountBook);

                }
            }
        }

    }
}
