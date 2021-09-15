package com.zzup.ctbupbit.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoinType3 {
    ADA("ADA"), // 에이다
    ANKR("ANKR"), // 앵커
    ATOM("ATOM"), // 코스모스
    BAT("BAT"), // 베이직어텐션토큰
    BCH("BCH"), // 비트코인캐시
    BCHA("BCHA"), // 비트코인캐시에이비씨
    BORA("BORA"), // 보라
    BSV("BSV"), // 비트코인에스브이
    BTC("BTC"), // 비트코인
    BTG("BTG"), // 비트코인골드
    BTT("BTT"), // 비트토렌트
    DOT("DOT"), // 폴카닷
    EOS("EOS"), // 이오스
    ETC("ETC"), // 이더리움클래식
    ETH("ETH"), // 이더리움
    ICX("ICX"), // 아이콘
    JST("JST"), // 저스트
    KNC("KNC"), // 카이버네트워크
    LINK("LINK"), // 체인링크
    LTC("LTC"), // 라이트코인
    MBL("MBL"), // 무비블록
    MLK("MLK"), // 밀크
    OBSR("OBSR"), // 옵저버
    OMG("OMG"), // 오미세고
    ONG("ONG"), // 온톨로지가스
    ONT("ONT"), // 온톨로지
    ORBS("ORBS"), // 오브스
    QTCON("QTCON"), // 퀴즈톡
    QTUM("QTUM"), // 퀀텀
    SRM("SRM"), // 세럼
    SXP("SXP"), // 스와이프
    TRX("TRX"), // 트론
    XLM("XLM"), // 스텔라루멘
    XRP("XRP"), // 리플
    XTZ("XTZ"), // 테조스
    ZIL("ZIL"), // 질리카
    ZRX("ZRX"), // 제로엑스
    ;

    private final String type;
}
