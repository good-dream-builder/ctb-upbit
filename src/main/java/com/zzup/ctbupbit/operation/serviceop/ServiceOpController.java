package com.zzup.ctbupbit.operation.serviceop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

@RestController
public class ServiceOpController {
    private ServiceOpService serviceOpService;

    ServiceOpController(ServiceOpService serviceOpService) {
        this.serviceOpService = serviceOpService;
    }

    @GetMapping("/op")
    public ServiceOp getServiceOp() {
        return serviceOpService.getServiceOp();
    }

    @PostMapping("/op")
    public ServiceOp setServiceOp(
            @RequestParam("opType") ServiceOpType opType,
            @RequestParam(value = "limitMoney", defaultValue = "3000000") Integer limitMoney,
            @RequestParam(value = "buyUnitPrice", defaultValue = "5001.0") Double buyUnitPrice,
            @RequestParam(value = "stopLossUnitPrice", defaultValue = "5001.0") Double stopLossUnitPrice) throws UnsupportedEncodingException, NoSuchAlgorithmException, ParseException {
        return serviceOpService.setServiceOp(opType, limitMoney, buyUnitPrice, stopLossUnitPrice);
    }
}
