package com.zzup.ctbupbit.provider.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderBook {
    private String market;    //마켓 코드	String
    private Long timestamp;    //호가 생성 시각	Long
    private Double total_ask_size;    //호가 매도 총 잔량	Double
    private Double total_bid_size;    //호가 매수 총 잔량	Double
    private List<OrderBookUnit> orderbook_units;    //호가	List of Objects
}
