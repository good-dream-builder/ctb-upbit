package com.zzup.ctbupbit.trx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzup.ctbupbit.accounting.AccountBook;
import com.zzup.ctbupbit.accounting.AccountBookRepository;
import com.zzup.ctbupbit.accounting.AccountingService;
import com.zzup.ctbupbit.common.DealType;
import com.zzup.ctbupbit.policy.CoinPolicyResult;
import com.zzup.ctbupbit.policy.PolicyContainer;
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
import java.util.*;

@Component
public class BuyTrx implements BaseTrx {
    public Logger logger = LoggerFactory.getLogger(BuyTrx.class);

    // [ Constants ]
//    private final Double UNIT_PRICE = 5001.0;
    private final Double FEE_RATE = 0.0005;

    // -30% 미만이면 사지 않는다.
    private final Double STOP_BUY_RATE = -0.30;

    // -1.5%면 추매 한다.
    private final Double LIMIT_RATE = 0.985;

    // 타겟 코인 갯수 비율(상위 %)
    private final Double TARGET_RATE = 1.0; // 100%

    // 연속 매수 제한 횟수
    private final int CONTINUOUS_LIMIT_COUNT = 4;

    // [ Injection ]
    private UpbitRestApiCaller api;
    private PolicyContainer policy;
    private AccountBookRepository accountBookRepository;
    private ServiceOpService serviceOpService;
    private AccountingService accountingService;
    private UrgencyOpService urgencyOpService;

    // [ Members ]
    private List<CoinPolicyResult> coinPolicyResultList;
    private String markets;

    BuyTrx(UpbitRestApiCaller api,
           PolicyContainer policy,
           ServiceOpService serviceOpService,
           AccountingService accountingService,
           AccountBookRepository accountBookRepository,
           UrgencyOpService urgencyOpService) {
        this.api = api;
        this.policy = policy;
        this.serviceOpService = serviceOpService;
        this.accountingService = accountingService;
        this.accountBookRepository = accountBookRepository;
        this.urgencyOpService = urgencyOpService;
    }

    @Override
    public void order() {
        // Step1. 내 계좌 정보를 가져온다.
        List<Account> accountList = api.getAccountList();
        Map<String, Account> accountMap = new HashMap<>();
        Account krwAccount = null;
        for (Account el : accountList) {
            final String currency = el.getCurrency();
            if (currency.equals("KRW")) {
                krwAccount = el;
            } else if (currency.equals("NPXS")) {
                // TODO 제외대상 추가 영역
            } else if (Double.parseDouble(el.getAvg_buy_price()) > 0) {
                UrgencyOp urgencyOp = urgencyOpService.getUrgencyOp();
                if (currency.equals(urgencyOp.getTarget()) == false) {
                    String market = "KRW-" + currency;
                    accountMap.put(market, el);
                }
            }
        }
//        logger.debug("BuyTrx::order::accountMap = " + accountMap);
        final Integer LIMIT_MONEY = serviceOpService.getLimitMoney();
        final Double UNIT_PRICE = serviceOpService.getBuyUnitPrice();
//        logger.debug("BuyTrx::order::LIMIT_MONEY = " + LIMIT_MONEY);
//        logger.debug("BuyTrx::order::UNIT_PRICE = " + UNIT_PRICE);

        Double myMoney = Double.parseDouble(krwAccount.getBalance());
        myMoney -= LIMIT_MONEY;
//        logger.debug("BuyTrx::order::myMoney = " + myMoney);

        // Step2. 전체 종목 중 Buy score가 높은 순서로 정리한다.
        Map<String, CoinPolicyResult> coinPolicyResultMap = policy.getCoinPolicyResultMap();
        coinPolicyResultList = new ArrayList<>(coinPolicyResultMap.values());
//        logger.debug("BuyTrx::order::coinPolicyResultList(original) = " + coinPolicyResultList);

        sortByScore();

        // Step3. TARGET_RATE 비율 만큼 구매한다. 현재 100%
        final int allCoinCount = coinPolicyResultList.size();
        final int targetCoinCount = (int) (allCoinCount * TARGET_RATE);
//        logger.debug("targetCoinCount = " + targetCoinCount);
        makeTarget(targetCoinCount);

        // Step4. 시장 현재 호가를 가져온다.
        List<OrderBook> orderBookList = getOrderBookList();
        if (orderBookList == null) return;

        // Step5. 주문한다.
        for (OrderBook orderBook : orderBookList) {
            if (myMoney > UNIT_PRICE) {

                final String market = orderBook.getMarket();
                OrderBookUnit orderBookUnit = orderBook.getOrderbook_units().get(0);
                final Double bidPrice = orderBookUnit.getBid_price(); // 시장 매수 호가
                final Double askPrice = orderBookUnit.getAsk_price(); // 시장 매도 호가

                // 산 적이 없는 경우는 무조건 사지도록.
                Double myLimitPrice = 10000000000000.0;
                Double lastPrice = 10000000000000.0;
                Double myPrice = askPrice;
//                Double prevAccFee = 0.0;
                Account myAccount = accountMap.get(market);
                if (myAccount != null) {
                    myPrice = Double.parseDouble(myAccount.getAvg_buy_price());

                    /**
                     * (정책) -30% 미만이면 매수 하지 않는다.
                     */
                    Double diffPrice = bidPrice - myPrice;
                    Double diffRate = diffPrice / myPrice;
                    if (diffRate < STOP_BUY_RATE) {
                        logger.debug(market + " : " + (STOP_BUY_RATE * 100) +"%를 초과하여 구매하지 않습니다. = " + (diffRate * 100));
                        continue;
                    }

//                    logger.debug("BuyTrx::order::myPrice = " + myPrice);

                    // 이전 거래 내역 3개를 가져온다.
                    List<AccountBook> accountBookList = accountingService.getLast5AccountBookListByCoin(market);
//                    logger.debug("BuyTrx::order::accountBookList = " + accountBookList);

                    // 연속으로 4번 매수 하였는지 확인한다.
                    boolean isContinuousBuy = false;
                    if (accountBookList != null) {
                        if (accountBookList.size() >= CONTINUOUS_LIMIT_COUNT) {
                            isContinuousBuy = true;

                            for (AccountBook el : accountBookList) {
                                if (el.getDeal() == DealType.SELL) {
                                    isContinuousBuy = false;
                                    break;
                                }
                            }
                        }
                    }

                    // 연속으로 4번 매수한 경우, 마지막 거래 가격으로 비교 기준가를 정한다.
                    if (isContinuousBuy == true) {
                        logger.debug(market + " : " + CONTINUOUS_LIMIT_COUNT +"번 이상 반복 구매하여, 가격을 조정합니다.");
                        if (accountBookList.get(0).getDeal() == DealType.BUY) {
                            lastPrice = accountBookList.get(0).getMarketPrice();
//                            logger.debug("BuyTrx::order::lastPrice = " + lastPrice);
                        }

                        // 더 작은 쪽을 myLimitPrice에 할당한다.
                        if (lastPrice < myPrice) {
                            myLimitPrice = lastPrice;
                        } else {
                            myLimitPrice = myPrice;
                        }
                    }

//                    logger.debug("BuyTrx::order::myLimitPrice = " + myLimitPrice);
                }

//                logger.debug("BuyTrx::order::myAccount = " + myAccount);
//                logger.debug("BuyTrx::order::bidPrice = " + bidPrice);
//                logger.debug("BuyTrx::order::askPrice = " + askPrice);

                // 시장 매수 호가가 제한가(현재가 혹은 마지막 거래가) 보다 -1.2% 이하 인 경우 에만 주문한다.
                myLimitPrice = myLimitPrice * LIMIT_RATE;
//                logger.debug("BuyTrx::order::myLimitPrice * LIMIT_RATE = " + myLimitPrice);
//                logger.debug("myLimitPrice = " + myLimitPrice + ", askPrice = " + askPrice);

                if (myLimitPrice > askPrice) {
                    // 1) 주문서 작성
                    final Double amount = UNIT_PRICE / askPrice;

                    OrderReq orderReq = new OrderReq();
                    orderReq.setMarket(market);
                    orderReq.setSide(OrderSide.BUY);
                    orderReq.setVolume(String.valueOf(amount));
                    orderReq.setPrice(String.valueOf(askPrice));
//                    logger.debug("BuyTrx::order::orderReq = " + orderReq);


                    // 2) 주문하기
                    OrderResp orderResp = null;
                    try {
                        orderResp = api.order(orderReq);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 계좌 조회를 계속할 수 없으므로, 임시적으로 산 금액을 제거해준다.
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

                    Double fee = UNIT_PRICE * FEE_RATE;
                    accountBook.setFee(fee);
//                    Double accFee = fee + prevAccFee;
//                    accountBook.setAccFee(accFee);

                    accountBookRepository.save(accountBook);

                }
            }

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
//        logger.debug("BuyTrx::order::markets = " + markets);

        List<OrderBook> orderBookList = null;
        if (markets.length() > 1) {
            orderBookList = api.showOrderBook(markets);
//            logger.debug("BuyTrx::order::orderBookList = " + orderBookList);
        }

        return orderBookList;
    }

    private void makeTarget(int count) {
        final String urgencyTarget = urgencyOpService.getUrgencyOp().getTarget();
        coinPolicyResultList = coinPolicyResultList.subList(0, count);
        List<CoinPolicyResult> targetCoinPolicyResultList = new ArrayList<>();
        for (CoinPolicyResult el : coinPolicyResultList) {
            final String marketCoinName = el.getMarketCoinName();

            if (urgencyTarget.length() > 1 && marketCoinName.contains(urgencyTarget)) {
                logger.debug(el.getMarketCoinName() + " : 매수 대상에서 제외합니다.");
                continue;
            }

            // 음수 경합은 대상이 아니다.
            if (el.getBuyScore() > 0 && el.getStopLossScore() < 1) {
                targetCoinPolicyResultList.add(el);
            }
            // TODO 모든 대상을 추가한다. 취소
            //targetCoinPolicyResultList.add(el);
        }

        coinPolicyResultList = targetCoinPolicyResultList;
//        logger.debug("BuyTrx::order::coinPolicyResultList(subList) = " + coinPolicyResultList);
    }

    private void sortByScore() {
        this.coinPolicyResultList.sort(new Comparator<CoinPolicyResult>() {
            @Override
            public int compare(CoinPolicyResult o1, CoinPolicyResult o2) {
                if (o1.getBuyScore() < o2.getBuyScore()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
//        logger.debug("BuyTrx::sortByScore::coinPolicyResultList = " + coinPolicyResultList);
    }
}
