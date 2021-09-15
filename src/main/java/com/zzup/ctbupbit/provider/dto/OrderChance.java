package com.zzup.ctbupbit.provider.dto;

import lombok.Data;

@Data
public class OrderChance {
    private String bid_fee;    //매수 수수료 비율	NumberString
    private String ask_fee;    //매도 수수료 비율	NumberString
    private String maker_bid_fee;
    private String maker_ask_fee;
    private Market market;
    private BidAccount bid_account;
    private AskAccount ask_account;
}
