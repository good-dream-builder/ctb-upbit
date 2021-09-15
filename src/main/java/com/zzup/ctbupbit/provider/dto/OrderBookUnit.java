package com.zzup.ctbupbit.provider.dto;

import lombok.Data;

@Data
public class OrderBookUnit {
    private Double ask_price;	//매도호가	Double
    private Double bid_price;	//매수호가	Double
    private Double ask_size;	//매도 잔량	Double
    private Double bid_size;	//매수 잔량	Double
}
