package com.zzup.ctbupbit.provider.dto;

import lombok.Data;

/**
 * 마켓에 대한 정보
 */
@Data
public class Market {
    private String id;      //마켓의 유일 키	String
    private String name;    //마켓 이름	String
    private String[] order_types;    //지원 주문 방식	Array[String]
    private String[] order_sides;    //지원 주문 종류	Array[String]
    private Bid bid;
    private Ask ask;
    private String max_total;    //최대 매도/매수 금액	NumberString
    private String state;
}
