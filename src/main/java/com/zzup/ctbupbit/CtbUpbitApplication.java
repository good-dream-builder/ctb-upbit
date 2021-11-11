package com.zzup.ctbupbit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class CtbUpbitApplication {

    public static void main(String[] args) {
        SpringApplication.run(CtbUpbitApplication.class, args);
    }

    @PostConstruct
    public void afterStarted() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        System.out.println("서버 현재 시각 : " + new Date());
    }

}
