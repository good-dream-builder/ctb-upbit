package com.zzup.ctbupbit.provider;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CallLimit {
    private String group;
    private Integer min;
    private Integer sec;
    private LocalDateTime lastCallTime;
}
