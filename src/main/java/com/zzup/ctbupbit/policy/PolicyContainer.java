package com.zzup.ctbupbit.policy;

import com.zzup.ctbupbit.accounting.AccountBookRepository;
import com.zzup.ctbupbit.common.CoinType;
import com.zzup.ctbupbit.provider.UpbitRestApiCaller;
import com.zzup.ctbupbit.provider.dto.Account;
import com.zzup.ctbupbit.operation.urgencyop.UrgencyOpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PolicyContainer {
    public Logger logger = LoggerFactory.getLogger(PolicyContainer.class);

    // [ Constants ]

    // [ Injection ]
    private UpbitRestApiCaller api;
    private AccountBookRepository accountBookRepository;
    private Hour24DirectionalPolicy hour24DirectionalPolicy;
    private Min15MovingDirectionalPolicy min15MovingDirectionalPolicy;
    private HistoricalPolicy historicalPolicy;
    private UrgencyOpService urgencyOpService;

    // [ Members ]
    private List<BasePolicy> allPolicyList;
    private List<BasePolicy> somePolicyList;
    private Map<String, CoinPolicyResult> coinPolicyResultMap;

    private String marketCoinNames;
    private Map<String, Account> accountMap;

    PolicyContainer(UpbitRestApiCaller api,
                    AccountBookRepository accountBookRepository,
                    UrgencyOpService urgencyOpService,
                    Hour24DirectionalPolicy hour24DirectionalPolicy,
                    Min15MovingDirectionalPolicy min15MovingDirectionalPolicy,
                    HistoricalPolicy historicalPolicy) {

        this.api = api;
        this.accountBookRepository = accountBookRepository;
        this.urgencyOpService = urgencyOpService;

        this.hour24DirectionalPolicy = hour24DirectionalPolicy;
        this.min15MovingDirectionalPolicy = min15MovingDirectionalPolicy;
        this.historicalPolicy = historicalPolicy;

        coinPolicyResultMap = new HashMap<>();
        StringBuffer stringBuffer = new StringBuffer();

        // 코인별 정책 결과를 저장할 객체들을 초기화 한다.
        int i = 0;
        for (CoinType coinType : CoinType.values()) {
            final String marketCoinName = "KRW-" + coinType.getType();
            stringBuffer.append(marketCoinName);
            if (i != (CoinType.values().length - 1)) {
                stringBuffer.append(",");
            }
            i++;

            CoinPolicyResult coinPolicyResult = new CoinPolicyResult();
            coinPolicyResult.setMarketCoinName(marketCoinName);
            coinPolicyResult.setStopLossScore(0);
            coinPolicyResult.setBuyScore(0);
            coinPolicyResult.setSellScore(0);

            coinPolicyResultMap.put(marketCoinName, coinPolicyResult);
        }
        marketCoinNames = stringBuffer.toString();

//        logger.debug("PolicyContainer::markets = " + marketCoinNames);
//        logger.debug("PolicyContainer::coinPolicyResultMap = " + coinPolicyResultMap);

        // 내 계좌 정보를 가져온다.
        List<Account> accountList = api.getAccountList();
        accountMap = new HashMap<>();
        Account krwAccount = null;
        for (Account el : accountList) {
            final String currency = el.getCurrency();
            if (currency.equals("KRW")) {
                krwAccount = el;
            } else if (currency.equals("NPXS")) {
                // TODO 제외대상 추가 영역
            } else if (Double.parseDouble(el.getAvg_buy_price()) > 0) {
                String market = "KRW-" + el.getCurrency();
                accountMap.put(market, el);
            }
        }
//        logger.debug("PolicyContainer::accountMap = " + accountMap);

        // 사용할 정책들을 저장한다.
        // 한번에 전체에 다 적용할 수 있는 정책
        allPolicyList = new ArrayList<>();
//        allPolicyList.add(this.hour24DirectionalPolicy);
//        allPolicyList.add(this.min15MovingDirectionalPolicy);
//        allPolicyList.add(this.historicalPolicy);
    }

    public void checkPolicy() {
        final String urgencyTarget = urgencyOpService.getUrgencyOp().getTarget();
        if (urgencyTarget.length() > 0) {
            makeMarketCoinNames();
            final String urgencyKey = "KRW-" + urgencyTarget;
            logger.info("[긴급] urgencyKey = " + urgencyKey);

            if (accountMap.containsKey(urgencyKey)) {
                logger.info("[긴급] " + urgencyKey + "를 임시 제거 합니다.");
                accountMap.remove(urgencyKey);
            }
        }

        for (BasePolicy policy : allPolicyList) {
            policy.decide(marketCoinNames, coinPolicyResultMap, accountMap);
        }
    }

    public Map<String, CoinPolicyResult> getCoinPolicyResultMap() {
        return coinPolicyResultMap;
    }

    public void resetCoinPolicyResult() {
        for (CoinPolicyResult el : coinPolicyResultMap.values()) {
            el.setSellScore(0);
            el.setBuyScore(0);
            el.setStopLossScore(0);

            coinPolicyResultMap.put(el.getMarketCoinName(), el);
        }
    }

    private void makeMarketCoinNames() {
        StringBuffer stringBuffer = new StringBuffer();
        coinPolicyResultMap.clear();

        int i = 0;
        for (CoinType coinType : CoinType.values()) {
            final String coinName = coinType.getType();
            final String urgencyTarget = urgencyOpService.getUrgencyOp().getTarget();
            if (coinName.equals(urgencyTarget)) {
                i++;
                continue;
            }

            final String marketCoinName = "KRW-" + coinName;
            stringBuffer.append(marketCoinName);
            if (i != (CoinType.values().length - 1)) {
                stringBuffer.append(",");
            }
            i++;

            CoinPolicyResult coinPolicyResult = new CoinPolicyResult();
            coinPolicyResult.setMarketCoinName(marketCoinName);
            coinPolicyResult.setStopLossScore(0);
            coinPolicyResult.setBuyScore(0);
            coinPolicyResult.setSellScore(0);

            coinPolicyResultMap.put(marketCoinName, coinPolicyResult);
        }
        marketCoinNames = stringBuffer.toString();
    }
}
