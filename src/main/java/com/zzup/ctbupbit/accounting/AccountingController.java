package com.zzup.ctbupbit.accounting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

@RestController
public class AccountingController {
    public Logger logger = LoggerFactory.getLogger(AccountingController.class);


    private AccountingService accountingService;

    AccountingController(AccountingService accountingService) {
        this.accountingService = accountingService;
    }

    @GetMapping("/profit")
    public Double profit(
            @RequestParam("fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate) throws UnsupportedEncodingException, NoSuchAlgorithmException, ParseException {
        return accountingService.getProfit(fromDate, toDate);
    }

//    @GetMapping("/profitOld")
    @Deprecated
    public Double profitOld(
            @RequestParam("fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate) throws UnsupportedEncodingException, NoSuchAlgorithmException, ParseException {
        return accountingService.getProfitOld(fromDate, toDate);
    }

    @GetMapping("/allProfit")
    public Double allProfit() throws UnsupportedEncodingException, NoSuchAlgorithmException, ParseException {
        return accountingService.getProfit();
    }
}
