package com.zzup.ctbupbit.accounting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface AccountBookRepository extends JpaRepository<AccountBook, Long> {

    @Query("SELECT SUM(ab.profit) FROM AccountBook ab WHERE ab.dateTime BETWEEN ?1 AND ?2")
    Double getProfitBetweenDate(Date from, Date to);

    AccountBook findFirstByCoinOrderByDateTimeDesc(String coin);

    List<AccountBook> findAllByDateTimeBetween(Date from, Date to);

    List<AccountBook> findTop3ByCoinOrderByIdDesc(String coin);

    List<AccountBook> findTop5ByCoinOrderByIdDesc(String coin);

    List<AccountBook> findAllByCoin(String coin);
}
