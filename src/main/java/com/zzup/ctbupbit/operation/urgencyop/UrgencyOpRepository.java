package com.zzup.ctbupbit.operation.urgencyop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrgencyOpRepository extends JpaRepository<UrgencyOp, Long> {
    UrgencyOp findFirstByOrderByIdDesc();
}