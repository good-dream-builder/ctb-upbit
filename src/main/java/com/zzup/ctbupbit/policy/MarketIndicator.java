package com.zzup.ctbupbit.policy;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class MarketIndicator {
    private MarketStatus marketStatus;
}
