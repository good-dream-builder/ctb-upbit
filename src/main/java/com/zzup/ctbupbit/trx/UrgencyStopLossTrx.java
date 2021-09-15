package com.zzup.ctbupbit.trx;

import com.zzup.ctbupbit.accounting.AccountBook;
import com.zzup.ctbupbit.accounting.AccountBookRepository;
import com.zzup.ctbupbit.accounting.AccountingService;
import com.zzup.ctbupbit.common.DealType;
import com.zzup.ctbupbit.policy.CoinPolicyResult;
import com.zzup.ctbupbit.provider.UpbitRestApiCaller;
import com.zzup.ctbupbit.provider.dto.*;
import com.zzup.ctbupbit.operation.serviceop.ServiceOpService;
import com.zzup.ctbupbit.operation.urgencyop.UrgencyOp;
import com.zzup.ctbupbit.operation.urgencyop.UrgencyOpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 긴급하게 거래 중지해야하는 것들 처리
 */
@Component
public class UrgencyStopLossTrx implements BaseTrx {
    public Logger logger = LoggerFactory.getLogger(UrgencyStopLossTrx.class);

    // [ Constants ]
    private Double STOP_LOSS_RATE;

    // [ Injection ]
    private UpbitRestApiCaller api;
    private UrgencyOpService urgencyOpService;
    private ServiceOpService serviceOpService;
    private AccountingService accountingService;
    private AccountBookRepository accountBookRepository;

    // [ Members ]
    private List<CoinPolicyResult> coinPolicyResultList;
    private Map<String, Account> accountMap;

    UrgencyStopLossTrx(UpbitRestApiCaller api,
                       UrgencyOpService urgencyOpService,
                       ServiceOpService serviceOpService,
                       AccountingService accountingService,
                       AccountBookRepository accountBookRepository) {
        this.api = api;
        this.urgencyOpService = urgencyOpService;
        this.serviceOpService = serviceOpService;
        this.accountingService = accountingService;
        this.accountBookRepository = accountBookRepository;
    }

    @Override
    public void order() {
        UrgencyOp urgencyOp = urgencyOpService.getUrgencyOp();
        STOP_LOSS_RATE = urgencyOp.getStopLossRate();
        String target = urgencyOp.getTarget();

        List<Account> accountList = api.getAccountList();

        for (Account el : accountList) {
            if (el.getCurrency().equals("KRW")) {
            } else if (Double.parseDouble(el.getAvg_buy_price()) > 0) {
                final String market = "KRW-" + el.getCurrency();
                if (target != null && target.length() > 0) {
                    if (market.contains(target) == false) {
                        continue;
                    }
                }
                logger.debug("market = " + market + ", target = " + target);

                List<OrderBook> orderBookList = api.showOrderBook(market);
                if (orderBookList == null) return;

                Double myPrice = Double.parseDouble(el.getAvg_buy_price());
                Double myAmount = Double.parseDouble(el.getBalance());
                logger.debug("origin myAmount = " + myAmount);

                myAmount *= urgencyOp.getAmountRate();
                logger.debug("change myAmount = " + myAmount);

                for (OrderBook orderBook : orderBookList) {
                    OrderBookUnit orderBookUnit = orderBook.getOrderbook_units().get(0);
                    final Double bidPrice = orderBookUnit.getBid_price(); // 시장 매수 호가
                    final Double askPrice = orderBookUnit.getAsk_price(); // 시장 매도 호가

                    Double diffPrice = bidPrice - myPrice;
                    Double diffRate = diffPrice / myPrice;
//                    logger.debug("StopLossTrx::order::diffPrice = " + diffPrice);
//                    logger.debug("StopLossTrx::order::diffRate = " + diffRate);

                    if (diffRate > STOP_LOSS_RATE) {
                        logger.debug(market + " : 손절 비율가에 도달하지 않았습니다. = " + diffRate);
                        continue;
                    }

                    // 1) 주문서 작성
                    OrderReq orderReq = new OrderReq();
                    orderReq.setMarket(market);
                    orderReq.setSide(OrderSide.SELL);
                    orderReq.setVolume(String.valueOf(myAmount));
                    orderReq.setPrice(String.valueOf(bidPrice));

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
                    accountBook.setFee(bidPrice * 0.0005);
                    accountBook.setMyPrice(myPrice);
                    accountBook.setMarketPrice(Double.parseDouble(orderResp.getPrice()));
                    accountBook.setTrxAmount(Double.parseDouble(orderResp.getVolume()));
                    accountBook.setDealUuid(orderResp.getUuid());

                    final String policyResult = "urgency stop loss";
                    accountBook.setPolicyResult(policyResult);
                    accountBookRepository.save(accountBook);
                }
            }
        }

    }
}
