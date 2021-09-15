package com.zzup.ctbupbit.operation.urgencyop;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class UrgencyOp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;

    private Double stopLossRate;

    private Double amountRate;

    private String target;

    @PrePersist
    void prePersist() {
        dateTime = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        dateTime = LocalDateTime.now();
    }
}
