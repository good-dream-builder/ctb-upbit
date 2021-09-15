package com.zzup.ctbupbit.provider.dto;

import lombok.Data;

/**
 * 매도 시 제약사항
 */
@Data
public class Ask {
    private String currency;    //화폐를 의미하는 영문 대문자 코드	String
    private String price_unit;    //주문금액 단위	String
    private String min_total;    //최소 매도/매수 금액	Number
}
