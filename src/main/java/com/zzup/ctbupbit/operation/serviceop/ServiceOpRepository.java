package com.zzup.ctbupbit.operation.serviceop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceOpRepository extends JpaRepository<ServiceOp, Long> {
    ServiceOp findFirstByOrderByIdDesc();
}
