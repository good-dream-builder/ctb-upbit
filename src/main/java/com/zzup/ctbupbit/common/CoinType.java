package com.zzup.ctbupbit.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoinType {
    BTC("BTC"), // 비트코인
    ETH("ETH"), // 이더리움
    SOL("SOL"), // 솔라나
    DOT("DOT"), // 폴카닷
    ATOM("ATOM"), // 코스모스
    QTUM("QTUM"), // 퀀텀
    THETA("THETA"), // 쎄타토큰
    SRM("SRM"), // 세럼
    MANA("MANA"), // 디센트럴랜드
    AQT("AQT"), // 알파쿼크
    ENJ("ENJ"), // 엔진코인
    EOS("EOS"), // 이오스
    _1INCH("1INCH"), // 1인치네트워크
    LSK("LSK"), // 리스크
    SXP("SXP"), // 스와이프
    STORJ("STORJ"), // 스토리지
    STX("STX"), // 스택스
    ARK("ARK"), // 아크
    ADA("ADA"), // 에이다
    MLK("MLK"), // 밀크
    PUNDIX("PUNDIX"), // 펀디엑스
    TRX("TRX"), // 트론
    SSX("SSX"), // 썸씽
    JST("JST"), // 저스트
    MED("MED"), // 메디블록
    STMX("STMX"), // 스톰엑스
    MVL("MVL"), // 엠블
    MBL("MBL"), // 무비블록
    BTT("BTT"), // 비트토렌트
    XEC("XEC"), // 비트코인캐시에이비씨, 이캐시
    ;

    private final String type;
}
