package com.zzup.ctbupbit.crawler;

import com.zzup.ctbupbit.provider.dto.CandleMinute;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
public class CoinPriceInfo {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Id
    private LocalDateTime kst;// 캔들 기준 시각(KST 기준)	 ex) 2021-02-27T01:58:00
    private String coin;// 마켓명 ex) KRW-BTC
    private Double openPrice;// 시가 ex) 54787000.00000000
    private Double closePrice;// 종가 ex) 54880000.00000000
    private Double highPrice;// 고가 ex) 54880000.00000000
    private Double lowPrice;// 저가 ex) 54721000.00000000
    private Double accTradeVolume;// 누적 거래량	 ex) 4.80869715
    private Double accTradePrice;// 누적 거래 금액	 ex) 263524214.40459000
    private LocalDateTime utc;// 캔들 기준 시각(UTC 기준)	 ex) 2021-02-26T16:58:00

    public void setCoinPriceInfo(CandleMinute candleMinute) {
        coin = candleMinute.getMarket().replace("KRW-", "");
        kst = LocalDateTime.parse(candleMinute.getCandle_date_time_kst());
        openPrice = candleMinute.getOpening_price();
        closePrice = candleMinute.getTrade_price();
        highPrice = candleMinute.getHigh_price();
        lowPrice = candleMinute.getLow_price();
        accTradeVolume = candleMinute.getCandle_acc_trade_volume();
        accTradePrice = candleMinute.getCandle_acc_trade_price();
        utc = LocalDateTime.parse(candleMinute.getCandle_date_time_utc());
    }
}
