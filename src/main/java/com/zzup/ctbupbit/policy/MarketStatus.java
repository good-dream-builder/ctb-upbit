package com.zzup.ctbupbit.policy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MarketStatus {
    RISE("RISE"),
    FALL("FALL");

    private final String status;
}
