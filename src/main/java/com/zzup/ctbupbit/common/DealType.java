package com.zzup.ctbupbit.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DealType {
    BUY("BUY"),
    SELL("SELL"),
    STOP_LOSS("STOP_LOSS");

    private final String type;
}
