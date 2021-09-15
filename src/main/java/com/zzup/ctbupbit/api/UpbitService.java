package com.zzup.ctbupbit.api;

import com.zzup.ctbupbit.accounting.AccountBookRepository;
import com.zzup.ctbupbit.trx.SellTrx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpbitService {
    public Logger logger = LoggerFactory.getLogger(UpbitService.class);

    private AccountBookRepository accountBookRepository;
    private SellTrx sellTrx;

    UpbitService(AccountBookRepository accountBookRepository,
                 SellTrx sellTrx) {
        this.accountBookRepository = accountBookRepository;
        this.sellTrx = sellTrx;
    }

    public void sell() {
        sellTrx.order();
    }


}
