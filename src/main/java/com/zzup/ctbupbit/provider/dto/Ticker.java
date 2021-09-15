package com.zzup.ctbupbit.provider.dto;

import lombok.Data;

@Data
public class Ticker {
    private String market;          // 종목 구분 코드 ex) KRW-BTC
    private String trade_date;      // 최근 거래 일자(UTC) ex) 20210225,
    private String trade_time;      // 최근 거래 시각(UTC) ex) 173314,
    private String trade_date_kst;  // 최근 거래 일자(KST) ex) 20210226,
    private String trade_time_kst;  // 최근 거래 시각(KST) ex) 023314,
    private Double trade_timestamp; // 최근 거래 시각 ex) 1614274394000,
    private Double opening_price;   // 시가 ex) 56501000.00000000,
    private Double high_price;      // 고가 ex) 58890000.00000000,
    private Double low_price;       // 저가 ex)55449000.00000000,
    private Double trade_price;     // 종가 ex)56957000.0,
    private Double prev_closing_price;  // 전일 종가 ex)56500000.00000000,
    private String change;              // EVEN : 보합, RISE : 상승, FALL : 하락
    private Double change_price;        // 변화액의 절대값	 ex)457000.00000000,
    private Double change_rate;         // 변화율의 절대값 ex)0.0080884956,
    private Double signed_change_price; // 부호가 있는 변화액	 ex)457000.00000000,
    private Double signed_change_rate;  // 부호가 있는 변화율	 ex)0.0080884956,
    private Double trade_volume;        // 가장 최근 거래량	 ex)0.05591519,
    private Double acc_trade_price;     // 누적 거래대금(UTC 0시 기준)	 ex)686628425886.403500000,
    private Double acc_trade_price_24h; // 24시간 누적 거래대금	 ex)777897888988.10921,
    private Double acc_trade_volume;    // 누적 거래량(UTC 0시 기준)	 ex)12007.92970814,
    private Double acc_trade_volume_24h;    // 24시간 누적 거래량	 ex)13646.76426351,
    private Double highest_52_week_price;   // 52주 신고가	 ex)65985000.00000000,
    private String highest_52_week_date;    // 52주 신고가 달성일	 ex)2021-02-20,
    private Double lowest_52_week_price;    // 52주 신저가	 ex)5489000.00000000,
    private String lowest_52_week_date;     // 52주 신저가 달성일	 ex)2020-03-13,
    private Long timestamp;                 // 타임스탬프	 ex)1614274394741
}
