package com.zzup.ctbupbit.policy;

import lombok.Data;

@Data
public class CoinPolicyResult {
    private String marketCoinName;
    private Integer stopLossScore;
    private Integer buyScore;
    private Integer sellScore;
}
