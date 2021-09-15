package com.zzup.ctbupbit.operation.serviceop;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ServiceOp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceOpType op;

    private Integer limitMoney;

    private Double buyUnitPrice;

    private Double stopLossUnitPrice;

    @PrePersist
    void prePersist() {
        dateTime = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        dateTime = LocalDateTime.now();
    }
}
