package com.zzup.ctbupbit.provider.dto;

import lombok.Data;

/**
 * 일(Day) 캔들
 */
@Data
public class CandleMinute {
    private String market;// 마켓명 ex) KRW-BTC
    private String candle_date_time_utc;// 캔들 기준 시각(UTC 기준)	 ex) 2021-02-26T16:58:00
    private String candle_date_time_kst;// 캔들 기준 시각(KST 기준)	 ex) 2021-02-27T01:58:00
    private Double opening_price;// 시가 ex) 54787000.00000000
    private Double high_price;// 고가 ex) 54880000.00000000
    private Double low_price;// 저가 ex) 54721000.00000000
    private Double trade_price;// 종가 ex) 54880000.00000000
    private Long timestamp;// 해당 캔들에서 마지막 틱이 저장된 시각	 ex) 1614358718295
    private Double candle_acc_trade_price;// 누적 거래 금액	 ex) 263524214.40459000
    private Double candle_acc_trade_volume;// 누적 거래량	 ex) 4.80869715
    private Integer unit;// 분 단위(유닛)	ex) 1
}
