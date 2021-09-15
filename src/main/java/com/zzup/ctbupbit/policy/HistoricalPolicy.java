package com.zzup.ctbupbit.policy;

import com.zzup.ctbupbit.accounting.AccountBook;
import com.zzup.ctbupbit.accounting.AccountBookRepository;
import com.zzup.ctbupbit.common.DealType;
import com.zzup.ctbupbit.provider.UpbitRestApiCaller;
import com.zzup.ctbupbit.provider.dto.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 이전 매매 기록을 기준으로 연속된 거래를 제약
 */
@Component
public class HistoricalPolicy implements BasePolicy {
    public Logger logger = LoggerFactory.getLogger(HistoricalPolicy.class);

    // [ Constants ]

    // [ Injection ]
    private UpbitRestApiCaller api;
    private AccountBookRepository accountBookRepository;

    // [ Members ]

    HistoricalPolicy(UpbitRestApiCaller api,
                     AccountBookRepository accountBookRepository) {
        this.api = api;
        this.accountBookRepository = accountBookRepository;
    }

    @Override
    public void decide(String marketCoinNames,
                       Map<String, CoinPolicyResult> coinPolicyResultMap,
                       Map<String, Account> accountMap) {

        for (CoinPolicyResult coinPolicyResult : coinPolicyResultMap.values()) {
            final String marketCoinName = coinPolicyResult.getMarketCoinName();
            Account account = accountMap.get(marketCoinName);
            if (account == null) {
                continue;
            }

            int scoreUnit = (int) (Double.parseDouble(account.getAvg_buy_price()) * Double.parseDouble(account.getBalance()));
            int stopLossScore = coinPolicyResult.getStopLossScore();
            int buyScore = coinPolicyResult.getBuyScore();
            int sellScore = coinPolicyResult.getSellScore();

            List<AccountBook> accountBookList = accountBookRepository.findTop3ByCoinOrderByIdDesc(marketCoinName);

            if (accountBookList != null) {
                int listSize = accountBookList.size();

                if (listSize > 0) {
                    AccountBook first = accountBookList.get(0);
                    if (first.getDeal() == DealType.SELL) {
                        // 직전에 팔았으면 상승 중인 가능 성이 있어 사는데 포인트를 더한다.
                        buyScore += scoreUnit;
                        stopLossScore -= scoreUnit;
                    } else if (first.getDeal() == DealType.STOP_LOSS) {
                        // 직전에 손절 하였으면 한 동안은 사지 않는다.
                        buyScore -= scoreUnit;
                        stopLossScore += scoreUnit;
                    } else {
                        // 연속으로 2번 매수 한 경우
                        buyScore -= scoreUnit;
                    }

                    if (listSize > 1) {
                        AccountBook second = accountBookList.get(1);
                        if (second.getDeal() == DealType.SELL) {
                            // 상승 중인 가능 성이 있어 사는데 포인트를 더한다.
                            buyScore += (scoreUnit / 2);
                            stopLossScore -= (scoreUnit / 2);
                        } else if (second.getDeal() == DealType.STOP_LOSS) {
                            // 손절 하였으면 한 동안은 사지 않는다.
                            buyScore -= (scoreUnit / 2);
                            stopLossScore += (scoreUnit / 2);
                        } else {
                            // 연속으로 3번 매수 한 경우
                            buyScore -= (scoreUnit * 2);
                            stopLossScore += (scoreUnit / 3);
                        }

                        if (listSize > 2) {
                            AccountBook third = accountBookList.get(2);
                            if (third.getDeal() == DealType.SELL) {
                                // 상승 중인 가능 성이 있어 사는데 포인트를 더한다.
                                buyScore += (scoreUnit / 3);
                                stopLossScore -= (scoreUnit / 3);
                            } else if (third.getDeal() == DealType.STOP_LOSS) {
                                // 손절 하였으면 한 동안은 사지 않는다.
                                buyScore -= (scoreUnit / 3);
                                stopLossScore += (scoreUnit / 3);
                            } else {
                                // 연속으로 4번 매수 한 경우
                                buyScore -= (scoreUnit * 3);
                                stopLossScore += (scoreUnit / 4);
                            }
                        }
                    }
                }
            }

            coinPolicyResult.setStopLossScore(stopLossScore);
            coinPolicyResult.setBuyScore(buyScore);
            coinPolicyResult.setSellScore(sellScore);
            coinPolicyResultMap.put(marketCoinName, coinPolicyResult);
//            logger.debug("HistoricalPolicy::coinPolicyResult = " + coinPolicyResult);
        }

    }
}