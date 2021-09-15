package com.zzup.ctbupbit.policy;

import com.zzup.ctbupbit.provider.dto.Account;

import java.util.Map;

public interface BasePolicy {
    public void decide(String marketCoinNames,
                       Map<String, CoinPolicyResult> coinPolicyResultMap,
                       Map<String, Account> accountMap);
}