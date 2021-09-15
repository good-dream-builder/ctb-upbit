package com.zzup.ctbupbit.config.ipcheck;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPCheckRepository extends JpaRepository<IPCheck, Long> {
}
