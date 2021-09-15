package com.zzup.ctbupbit.config.ipcheck;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class IPCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;

    private String bannedIP;

    @PrePersist
    void prePersist() {
        dateTime = LocalDateTime.now();
    }
}
