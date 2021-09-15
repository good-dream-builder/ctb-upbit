package com.zzup.ctbupbit.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoinType {
    BTC("BTC"), // 비트코인
    ETH("ETH"), // 이더리움
    LTC("LTC"), // 라이트코인
    STRK("STRK"), // 스트라이크
    DOT("DOT"), // 폴카닷
    ATOM("ATOM"), // 코스모스
    LINK("LINK"), // 체인링크
    QTUM("QTUM"), // 퀀텀
    SRM("SRM"), // 세럼
    OMG("OMG"), // 오미세고
    EOS("EOS"), // 이오스
    ADA("ADA"), // 에이다
    STX("STX"), // 스택스
    XRP("XRP"), // 리플
    VET("VET"), // 비체인
    ;

    private final String type;
}
