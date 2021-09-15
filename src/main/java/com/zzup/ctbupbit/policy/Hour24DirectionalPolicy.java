package com.zzup.ctbupbit.policy;

import com.zzup.ctbupbit.accounting.AccountBookRepository;
import com.zzup.ctbupbit.provider.UpbitRestApiCaller;
import com.zzup.ctbupbit.provider.dto.Account;
import com.zzup.ctbupbit.provider.dto.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 24시간 기준 방향성 (1점 * 거래량)
 */
@Component
public class Hour24DirectionalPolicy implements BasePolicy {
    public Logger logger = LoggerFactory.getLogger(Hour24DirectionalPolicy.class);
    // [ Constants ]
    private final int UNIT_POINT = 1;

    // [ Injection ]
    private UpbitRestApiCaller api;
    private AccountBookRepository accountBookRepository;
    private MarketIndicator marketIndicator;

    // [ Members ]
    private String marketCoinNames;
    private Map<String, CoinPolicyResult> coinPolicyResultMap;
    private Map<String, Account> accountMap;
    private List<Ticker> tickerList;

    Hour24DirectionalPolicy(UpbitRestApiCaller api,
                            AccountBookRepository accountBookRepository,
                            MarketIndicator marketIndicator) {
        this.api = api;
        this.accountBookRepository = accountBookRepository;
        this.marketIndicator = marketIndicator;
    }

    @Override
    public void decide(String marketCoinNames,
                       Map<String, CoinPolicyResult> coinPolicyResultMap,
                       Map<String, Account> accountMap) {
        this.marketCoinNames = marketCoinNames;
        this.coinPolicyResultMap = coinPolicyResultMap;
        this.accountMap = accountMap;

        tickerList = api.getTickerList(marketCoinNames);
        /*
        tickerList.sort(new Comparator<Ticker>() {
            @Override
            public int compare(Ticker o1, Ticker o2) {
                if (o1.getAcc_trade_price_24h() < o2.getAcc_trade_price_24h()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
         */
//        logger.debug("AllDirectionalPolicy::tickerList = " + tickerList);

        int tickerIdx = tickerList.size();

        int allCount = tickerList.size();
        int riseCount = 0;
        int maxStopLossScore = 0;
        for (Ticker ticker : tickerList) {
//            logger.debug("AllDirectionalPolicy::tickerIdx = " + tickerIdx);

            String marketCoinName = ticker.getMarket();
            CoinPolicyResult coinPolicyResult = coinPolicyResultMap.get(marketCoinName);
            int stopLossScore = coinPolicyResult.getStopLossScore();
            int buyScore = coinPolicyResult.getBuyScore();
            int sellScore = coinPolicyResult.getSellScore();

            if (ticker.getChange().equals("RISE")) {
                riseCount += 1;
                buyScore += ticker.getChange_price();
//                buyScore += (UNIT_POINT * tickerIdx);
//                stopLossScore -= (UNIT_POINT * tickerIdx);
            } else if (ticker.getChange().equals("FALL")) {
                stopLossScore += ticker.getChange_price();
                if(maxStopLossScore < stopLossScore) {
                    maxStopLossScore = stopLossScore;
                }
//                buyScore -= (UNIT_POINT * tickerIdx);
//                stopLossScore += (UNIT_POINT * tickerIdx);
            } else {

            }

            coinPolicyResult.setStopLossScore(stopLossScore);
            coinPolicyResult.setBuyScore(buyScore);
            coinPolicyResult.setSellScore(sellScore);
            coinPolicyResultMap.put(marketCoinName, coinPolicyResult);

            tickerIdx--;
        }

        // riseRate가 35% 미만이면 전체적으로 하락장이란 소리이므로 매수를 줄인다.
        Double riseRate = (double) riseCount / (double) allCount;
//        logger.debug("AllDirectionalPolicy::riseRate = " + riseRate + ", allCount = " + allCount + ", riseCount = " + riseCount);

        if(riseRate > 49.9) {
//            logger.debug("상승장");
            marketIndicator.setMarketStatus(MarketStatus.RISE);
        } else {
//            logger.debug("하락장");
            marketIndicator.setMarketStatus(MarketStatus.FALL);
        }

        maxStopLossScore *= riseRate;

        if(riseRate < 0.35) {
            for(CoinPolicyResult el : coinPolicyResultMap.values()) {
                CoinPolicyResult cpr = el;
                String marketCoinName = cpr.getMarketCoinName();
                Integer buyScore = cpr.getBuyScore();
                buyScore -= maxStopLossScore;
                cpr.setBuyScore(buyScore);
                coinPolicyResultMap.put(marketCoinName, cpr);
            }
//            logger.debug("AllDirectionalPolicy::coinPolicyResultMap = " + coinPolicyResultMap);
        }



    }
}
