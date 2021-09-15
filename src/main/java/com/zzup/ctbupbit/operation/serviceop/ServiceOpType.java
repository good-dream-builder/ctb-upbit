package com.zzup.ctbupbit.operation.serviceop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceOpType {
    START("START"),
    FORCE("FORCE"),
    STOP("STOP");

    private final String type;
}
