package com.zzup.ctbupbit.provider;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Upbit의 API 콜 제한
 * https://docs.upbit.com/docs/user-request-guide
 */
@Component
public class CallLimitContainer {
    public Logger logger = LoggerFactory.getLogger(CallLimitContainer.class);

    private Map<String, CallLimit> callLimitMap;
    private final int MILLI_TO_NANO_UNIT = 1000000;

    CallLimitContainer() {
        callLimitMap = new HashMap<>();
    }

    public void updateCallLimit(HttpResponse response) {
        String group = "";
        int min = 0;
        int sec = 0;

        Header[] headers = response.getHeaders("Remaining-Req");
        Header header = headers[0];
        String headerValue = header.getValue();
        String[] valArr = headerValue.split(";");
        for (String el : valArr) {
            String[] param = el.split("=");
            if (param[0].contains("group")) {
                group = param[1];
            }

            if (param[0].contains("min")) {
                min = Integer.valueOf(param[1]);
            }

            if (param[0].contains("sec")) {
                sec = Integer.valueOf(param[1]);
            }
        }

        CallLimit callLimit = callLimitMap.get(group);
        // 서비스 운영 중 제일 처음 동작하는 경우
        if (callLimit == null) {
            CallLimit newCallLimit = new CallLimit();
            newCallLimit.setGroup(group);
            newCallLimit.setMin(min);
            newCallLimit.setSec(sec);
            newCallLimit.setLastCallTime(LocalDateTime.now());

            callLimitMap.put(group, newCallLimit);
        } else {
            callLimit.setGroup(group);
            callLimit.setMin(min);
            callLimit.setSec(sec);

            // 마지막 호출 시간은 api를 사용하기 위해
            // waitForAvailableCallApi 에서 일정 시간 멈춘 이후 세팅한다.

            callLimitMap.put(group, callLimit);
        }

//        logger.debug("updateCallLimit = " + callLimitMap.values());
    }

    public boolean canICallApi(String group) {
        CallLimit callLimit = callLimitMap.get(group);
        // 서비스 운영 중 제일 처음 동작하는 경우
        if (callLimit == null) {
            return true;
        }
        if (callLimit.getMin() > 1 && callLimit.getSec() > 1) {
            return true;
        } else {
           return false;
        }
    }

    public void waitForAvailableCallApi(String groupName) {
        CallLimit callLimit = callLimitMap.get(groupName);
        LocalDateTime lastCallTime = callLimit.getLastCallTime();
        LocalDateTime nowCallTime = LocalDateTime.now();

        // 최초 api 호출 시간 이후의 잔량 시간 만큼 쉬어서
        // api call 가능 갯수를 초기화 시킨다.
        Duration duration = Duration.between(lastCallTime, nowCallTime);
        int diffNanoSec = duration.getNano();
        int diffMilliSec = diffNanoSec / MILLI_TO_NANO_UNIT;

        try {
            Thread.sleep(1000 - diffMilliSec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        callLimit.setLastCallTime(LocalDateTime.now());
        callLimitMap.put(groupName, callLimit);
    }
}
