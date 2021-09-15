package com.zzup.ctbupbit.accounting;

import com.zzup.ctbupbit.common.DealType;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class AccountBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dateTime;

    private String coin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DealType deal;

    private Double myPrice;

    private Double marketPrice;

    private Double trxAmount;

    private Double profit; // 수수료를 제외한 실제 수익

    private Double fee; // 수수료

//    private Double accFee; // 매도 전 매수 누적 수수료

    private String dealUuid;

    private String policyResult;

    @PrePersist
    void persist() {
        if (deal == DealType.STOP_LOSS || deal == DealType.SELL) {
            profit = ((marketPrice - myPrice) * trxAmount) - fee;
        } else {
            profit = 0 - fee;
        }
    }
}
