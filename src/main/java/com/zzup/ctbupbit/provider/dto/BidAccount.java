package com.zzup.ctbupbit.provider.dto;

import lombok.Data;

/**
 * 매수 시 사용하는 화폐의 계좌 상태
 */
@Data
public class BidAccount {
    private String currency;    //화폐를 의미하는 영문 대문자 코드	String
    private String balance;    //주문가능 금액/수량	NumberString
    private String locked;    //주문 중 묶여있는 금액/수량	NumberString
    private String avg_buy_price;    //매수평균가	NumberString
    private Boolean avg_buy_price_modified;    //매수평균가 수정 여부	Boolean
    private String unit_currency;    //평단가 기준 화폐	String
}
