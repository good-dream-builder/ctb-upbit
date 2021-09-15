package com.zzup.ctbupbit.operation.serviceop;

import org.springframework.stereotype.Service;

@Service
public class ServiceOpService {
    private ServiceOp serviceOp;
    private ServiceOpRepository serviceOpRepository;

    ServiceOpService(ServiceOpRepository serviceOpRepository) {
        this.serviceOpRepository = serviceOpRepository;

        serviceOp = serviceOpRepository.findFirstByOrderByIdDesc();
        if (serviceOp == null) {
            serviceOp = new ServiceOp();
            serviceOp.setLimitMoney(777777);
            serviceOp.setBuyUnitPrice(5001.0);
            serviceOp.setStopLossUnitPrice(5001.0);
        }
    }

    public ServiceOp getServiceOp() {
        return serviceOp;
    }

    public Integer getLimitMoney() {
        return serviceOp.getLimitMoney();
    }

    public Double getBuyUnitPrice() {
        return serviceOp.getBuyUnitPrice();
    }

    public Double getStopLossUnitPrice() {
        return serviceOp.getStopLossUnitPrice();
    }

    public ServiceOpType getServiceOpType() {
        return serviceOp.getOp();
    }



    public ServiceOp getServiceOpFromDB() {
        return serviceOpRepository.findFirstByOrderByIdDesc();
    }

    public ServiceOp setServiceOp(ServiceOpType opType, Integer limitMoney, Double buyUnitPrice, Double stopLossUnitPrice) {
        serviceOp.setOp(opType);
        serviceOp.setLimitMoney(limitMoney);
        serviceOp.setBuyUnitPrice(buyUnitPrice);
        serviceOp.setStopLossUnitPrice(stopLossUnitPrice);

        serviceOp = serviceOpRepository.save(serviceOp);
        return serviceOp;
    }
}
