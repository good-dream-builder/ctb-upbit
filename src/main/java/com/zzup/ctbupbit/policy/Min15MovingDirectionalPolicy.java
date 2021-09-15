package com.zzup.ctbupbit.policy;

import com.zzup.ctbupbit.accounting.AccountBookRepository;
import com.zzup.ctbupbit.provider.UpbitRestApiCaller;
import com.zzup.ctbupbit.provider.dto.Account;
import com.zzup.ctbupbit.provider.dto.CandleMinute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * @TODO 테스트 필요.
 * 15분 평균선을 임의로 만들어서,
 * 현재 1분간의 종가가 높은가, 낮은가로 평가.(3점 * 거래량)
 */
@Component
public class Min15MovingDirectionalPolicy implements BasePolicy {
    public Logger logger = LoggerFactory.getLogger(Min15MovingDirectionalPolicy.class);

    // [ Constants ]
    private Double UNIT_POINT = 1.0;
    private final int CANDLE_COUNT = 10;

    // [ Injection ]
    private UpbitRestApiCaller api;
    private AccountBookRepository accountBookRepository;

    // [ Members ]
    private String marketCoinNames;
    private Map<String, CoinPolicyResult> coinPolicyResultMap;
    private Map<String, Account> accountMap;

    Min15MovingDirectionalPolicy(UpbitRestApiCaller api,
                                 AccountBookRepository accountBookRepository) {
        this.api = api;
        this.accountBookRepository = accountBookRepository;
    }

    @Override
    public void decide(String marketCoinNames,
                       Map<String, CoinPolicyResult> coinPolicyResultMap,
                       Map<String, Account> accountMap) {
        this.marketCoinNames = marketCoinNames;
        this.coinPolicyResultMap = coinPolicyResultMap;
        this.accountMap = accountMap;

        try {
            for (CoinPolicyResult coinPolicyResult : coinPolicyResultMap.values()) {
                String marketCoinName = coinPolicyResult.getMarketCoinName();
                int stopLossScore = coinPolicyResult.getStopLossScore();
                int buyScore = coinPolicyResult.getBuyScore();
                int sellScore = coinPolicyResult.getSellScore();

                List<CandleMinute> candle15MinuteList = api.getMinuteCandleList(coinPolicyResult.getMarketCoinName(), 15, CANDLE_COUNT);
//                logger.debug("SomeDirectionalPolicy::candle15MinuteList = " + candle15MinuteList);

                Double sumPrice = 0.0;
                Double sumVolume = 0.0;
                for (CandleMinute candleMinute : candle15MinuteList) {
                    sumPrice += candleMinute.getCandle_acc_trade_price();
                    sumVolume += candleMinute.getCandle_acc_trade_volume();
                }
                Double avgPrice = sumPrice / CANDLE_COUNT;
                Double avgVolume = sumVolume / CANDLE_COUNT;


                List<CandleMinute> candle1MinuteList = api.getMinuteCandleList(coinPolicyResult.getMarketCoinName(), 1, 1);
//                logger.debug("SomeDirectionalPolicy::candle1MinuteList = " + candle1MinuteList);

                Double curPrice = candle1MinuteList.get(0).getTrade_price();


//                UNIT_POINT = avgVolume;

                if (curPrice < avgPrice) {
                    buyScore += (UNIT_POINT * avgVolume);
                    stopLossScore -= (UNIT_POINT * avgVolume);
                } else if (curPrice >= avgPrice) {
                    buyScore -= (UNIT_POINT * avgVolume);
                    stopLossScore += (UNIT_POINT * avgVolume);
                } else {

                }

                coinPolicyResult.setStopLossScore(stopLossScore);
                coinPolicyResult.setBuyScore(buyScore);
                coinPolicyResult.setSellScore(sellScore);
                coinPolicyResultMap.put(marketCoinName, coinPolicyResult);

//                logger.debug("SomeDirectionalPolicy::coinPolicyResult = " + coinPolicyResult);
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
