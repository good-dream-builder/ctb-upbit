package com.zzup.ctbupbit.crawler;

import com.zzup.ctbupbit.policy.Hour24DirectionalPolicy;
import com.zzup.ctbupbit.provider.UpbitRestApiCaller;
import com.zzup.ctbupbit.provider.dto.CandleMinute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
public class CoinPriceInfoController {
    public Logger logger = LoggerFactory.getLogger(CoinPriceInfoController.class);

    private UpbitRestApiCaller api;
    private CoinPriceInfoRepository coinPriceInfoRepository;

    CoinPriceInfoController(
            UpbitRestApiCaller api,
            CoinPriceInfoRepository coinPriceInfoRepository){
        this.api = api;
        this.coinPriceInfoRepository = coinPriceInfoRepository;
    }

    @GetMapping("/saveMin")
    void saveMinCandleToDB() throws NoSuchAlgorithmException, IOException {
        String lastDate = "";

        List<CandleMinute> candleMinuteList = null;
        while (true) {
            logger.debug("lastDate = " + lastDate);
            final String paramLastDate = lastDate.replace("T", " ");
            candleMinuteList = api.getMinuteCandleList("KRW-BTC", 1, 200, paramLastDate);
            if (lastDate.equals(candleMinuteList.get(candleMinuteList.size() - 1))) {
                break;
            }
            for (CandleMinute el : candleMinuteList) {
                CoinPriceInfo coinPriceInfo = new CoinPriceInfo();
                coinPriceInfo.setCoinPriceInfo(el);
                coinPriceInfoRepository.save(coinPriceInfo);
                lastDate = el.getCandle_date_time_utc();
            }

        }
    }
}
