package com.zzup.ctbupbit.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoinType {
    BTC("BTC"), // 비트코인
    SOL("SOL"), // 솔라나
    QTUM("QTUM"), // 퀀텀
    DOT("DOT"), // 폴카닷
    ATOM("ATOM"), // 코스모스
    SRM("SRM"), // 세럼
    STX("STX"), // 스택스
    VET("VET"), // 비체인
    EOS("EOS"), // 이오스
//    XRP("XRP"), // 리플
    ;

    private final String type;
}
