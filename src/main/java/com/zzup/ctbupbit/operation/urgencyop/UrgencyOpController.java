package com.zzup.ctbupbit.operation.urgencyop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

@RestController
public class UrgencyOpController {
    private UrgencyOpService urgencyOpService;

    UrgencyOpController(UrgencyOpService urgencyOpService) {
        this.urgencyOpService = urgencyOpService;
    }

    @GetMapping("/urgencyOp")
    public UrgencyOp getUrgencyOp() {
        return urgencyOpService.getUrgencyOp();
    }

    @PostMapping("/urgencyOp")
    public UrgencyOp setUrgencyOp(
            @RequestBody UrgencyOp urgencyOp
    ) throws UnsupportedEncodingException, NoSuchAlgorithmException, ParseException {
        return urgencyOpService.setUrgencyOp(urgencyOp);
    }
}
