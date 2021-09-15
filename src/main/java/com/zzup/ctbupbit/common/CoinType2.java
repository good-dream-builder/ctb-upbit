package com.zzup.ctbupbit.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoinType2 {
    BTC("BTC"), // 비트코인
    ETH("ETH"), // 이더리움
    NEO("NEO"), // 네오
    MTL("MTL"), // 메탈
    LTC("LTC"), // 라이트코인
    //XRP("XRP"), // 리플, TODO 소송중이라 뺌.
    ETC("ETC"), // 이더리움클래식
    OMG("OMG"), // 오미세고
    SNT("SNT"), // 스테이터스네트워크토큰
    WAVES("WAVES"), // 웨이브
    XEM("XEM"), // 넴
    QTUM("QTUM"), // 퀀텀
    LSK("LSK"), // 리스크
    STEEM("STEEM"), // 스팀
    XLM("XLM"), // 스텔라루멘
    ARDR("ARDR"), // 아더
    KMD("KMD"), // 코모도
    ARK("ARK"), // 아크
    STORJ("STORJ"), // 스토리지
    GRS("GRS"), // 그로스톨코인
    REP("REP"), // 어거
    EMC2("EMC2"), // 아인스타이늄
    ADA("ADA"), // 에이다
    STX("STX"), // 스택스
    SBD("SBD"), // 스팀달러
    POWR("POWR"), // 파워렛저
    EOS("EOS"), // 이오스
    ICX("ICX"), // 아이콘
    TRX("TRX"), // 트론
    SC("SC"), // 시아코인
    IGNIS("IGNIS"), // 이그니스
    ONT("ONT"), // 온톨로지
    ZIL("ZIL"), // 질리카
    POLY("POLY"), // 폴리매쓰
    ZRX("ZRX"), // 제로엑스
    LOOM("LOOM"), // 룸네트워크
    BCH("BCH"), // 비트코인캐시
    ADX("ADX"), // 애드엑스
    BAT("BAT"), // 베이직어텐션토큰
    IOST("IOST"), // 아이오에스티
    DMT("DMT"), // 디마켓
    RFR("RFR"), // 리퍼리움
    CVC("CVC"), // 시빅
    IQ("IQ"), // 에브리피디아
    IOTA("IOTA"), // 아이오타
    MFT("MFT"), // 메인프레임
    ONG("ONG"), // 온톨로지가스
    GAS("GAS"), // 가스
    UPP("UPP"), // 센티넬프로토콜
    ELF("ELF"), // 엘프
    KNC("KNC"), // 카이버네트워크
    BSV("BSV"), // 비트코인에스브이
    THETA("THETA"), // 쎄타토큰
    EDR("EDR"), // 엔도르
    QKC("QKC"), // 쿼크체인
    BTT("BTT"), // 비트토렌트
    MOC("MOC"), // 모스코인
    ENJ("ENJ"), // 엔진코인
    TFUEL("TFUEL"), // 쎄타퓨엘
    MANA("MANA"), // 디센트럴랜드
    ANKR("ANKR"), // 앵커
    PUNDIX("PUNDIX"), // 펀디엑스
    AERGO("AERGO"), // 아르고
    ATOM("ATOM"), // 코스모스
    TT("TT"), // 썬더토큰
    CRE("CRE"), // 캐리프로토콜
    SOLVE("SOLVE"), // 솔브케어
    MBL("MBL"), // 무비블록
    TSHP("TSHP"), // 트웰브쉽스
    WAXP("WAXP"), // 왁스
    HBAR("HBAR"), // 헤데라해시그래프
    MED("MED"), // 메디블록
    MLK("MLK"), // 밀크
    STPT("STPT"), // 에스티피
    ORBS("ORBS"), // 오브스
    VET("VET"), // 비체인
    CHZ("CHZ"), // 칠리즈
    PXL("PXL"), // 픽셀
    STMX("STMX"), // 스톰엑스
    DKA("DKA"), // 디카르고
    HIVE("HIVE"), // 하이브
    KAVA("KAVA"), // 카바
    AHT("AHT"), // 아하토큰
    STRK("STRK"), // 스트라이크
    LINK("LINK"), // 체인링크
    XTZ("XTZ"), // 테조스
    BORA("BORA"), // 보라
    JST("JST"), // 저스트
    CRO("CRO"), // 크립토닷컴체인
    TON("TON"), // 톤
    SXP("SXP"), // 스와이프
    LAMB("LAMB"), // 람다
    HUNT("HUNT"), // 헌트
    MARO("MARO"), // 마로
    PLA("PLA"), // 플레이댑
    DOT("DOT"), // 폴카닷
    SRM("SRM"), // 세럼
    MVL("MVL"), // 엠블
    PCI("PCI"), // 페이코인
    STRAX("STRAX"), // 스트라티스
    AQT("AQT"), // 알파쿼크
    BCHA("BCHA"), // 비트코인캐시에이비씨
    GLM("GLM"), // 골렘
    QTCON("QTCON"), // 퀴즈톡
    SSX("SSX"), // 썸씽
    META("META"), // 메타디움
    OBSR("OBSR"), // 옵저버
    FCT2("FCT2"), // 피르마체인
    LBC("LBC"), // 엘비알와이크레딧
    CBK("CBK"), // 코박토큰
    SAND("SAND"), // 샌드박스
    HUM("HUM"), // 휴먼스케이프
    BTG("BTG"), // 비트코인골드
    DOGE("DOGE") // 도지코인
    ;
    private final String type;
}
