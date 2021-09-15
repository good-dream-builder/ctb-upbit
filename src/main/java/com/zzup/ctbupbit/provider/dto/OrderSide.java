package com.zzup.ctbupbit.provider.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderSide {
    BUY("bid"), SELL("ask");
    private final String type;
}
