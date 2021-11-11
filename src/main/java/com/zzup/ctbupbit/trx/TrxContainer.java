package com.zzup.ctbupbit.trx;

import com.zzup.ctbupbit.api.UpbitService;
import com.zzup.ctbupbit.operation.serviceop.ServiceOpType;
import com.zzup.ctbupbit.policy.PolicyContainer;
import com.zzup.ctbupbit.operation.serviceop.ServiceOpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrxContainer {
    public Logger logger = LoggerFactory.getLogger(TrxContainer.class);

    /// [ Constants ]

    // [ Injection ]
    private UpbitService upbitService;
    private ServiceOpService serviceOpService;
    private PolicyContainer policy;
    private SellTrx sellTrx;
    private BuyTrx buyTrx;
    private StopLossTrx stopLossTrx;
    private UrgencyStopLossTrx urgencyStopLossTrx;
    private ScaleTradingTrx scaleTradingTrx;

    // [ Members ]
    private List<BaseTrx> trxList;
    private List<BaseTrx> changeTrxList;

    TrxContainer(UpbitService upbitService,
                 ServiceOpService serviceOpService,
                 PolicyContainer policy,
                 SellTrx sellTrx,
                 BuyTrx buyTrx,
                 StopLossTrx stopLossTrx,
                 UrgencyStopLossTrx urgencyStopLossTrx,
                 ScaleTradingTrx scaleTradingTrx) {
        this.upbitService = upbitService;
        this.serviceOpService = serviceOpService;

        this.policy = policy;
        this.sellTrx = sellTrx;
        this.buyTrx = buyTrx;
        this.stopLossTrx = stopLossTrx;
        this.urgencyStopLossTrx = urgencyStopLossTrx;
        this.scaleTradingTrx = scaleTradingTrx;

        trxList = new ArrayList<>();
//        trxList.add(this.sellTrx);
        trxList.add(this.buyTrx);

        changeTrxList = new ArrayList<>();
        changeTrxList.add(this.sellTrx);
//        changeTrxList.add(this.stopLossTrx);
//        changeTrxList.add(this.scaleTradingTrx);
    }


    //    @Scheduled(fixedDelayString = "12000")
    @Scheduled(fixedDelayString = "600000")
    public void trx() {
        ServiceOpType serviceOpType = serviceOpService.getServiceOpType();
        if (serviceOpType != null) {
            if (serviceOpType == ServiceOpType.STOP) {
                logger.debug("서비스를 일시 중지 합니다.");
                return;
            }
        }

        // 전체 코인들에 대한 정책을 확인한다.
        policy.checkPolicy();

        for (BaseTrx trx : trxList) {
            trx.order();
        }

        // 정책으로 정해진 점수를 초기화 한다.
        policy.resetCoinPolicyResult();
    }


    //    @Scheduled(cron = "0 0 0/1 * * ?")
//    @Scheduled(fixedDelayString = "12000")
    @Scheduled(fixedDelayString = "12000")
    public void changeTrx() {
        ServiceOpType serviceOpType = serviceOpService.getServiceOpType();
        if (serviceOpType != null) {
            if (serviceOpType == ServiceOpType.STOP) {
                logger.debug("서비스를 일시 중지 합니다.");
                return;
            }
        }

        for (BaseTrx trx : changeTrxList) {
            trx.order();
        }
    }

    public void urgencyTrx() {
        urgencyStopLossTrx.order();
    }

    public void scaleTrx() {
        scaleTradingTrx.order();
    }

}
