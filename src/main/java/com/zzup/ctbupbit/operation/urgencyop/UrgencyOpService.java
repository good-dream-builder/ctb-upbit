package com.zzup.ctbupbit.operation.urgencyop;

import org.springframework.stereotype.Service;

@Service
public class UrgencyOpService {
    private UrgencyOp urgencyOp;
    private UrgencyOpRepository urgencyOpRepository;

    UrgencyOpService(UrgencyOpRepository urgencyOpRepository) {
        this.urgencyOpRepository = urgencyOpRepository;

        urgencyOp = urgencyOpRepository.findFirstByOrderByIdDesc();
        if (urgencyOp == null) {
            urgencyOp = new UrgencyOp();
            urgencyOp.setStopLossRate(-0.2);    // -20.0%
            urgencyOp.setAmountRate(0.5);       // 50%
            urgencyOp.setTarget("");
        }
    }

    public UrgencyOp getUrgencyOp() {
        return urgencyOp;
    }

    public UrgencyOp setUrgencyOp(UrgencyOp urgencyOp) {
        this.urgencyOp = urgencyOp;
        return urgencyOpRepository.save(urgencyOp);
    }
}
