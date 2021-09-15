package com.zzup.ctbupbit.api;

import com.zzup.ctbupbit.policy.PolicyContainer;
import com.zzup.ctbupbit.provider.UpbitRestApiCaller;
import com.zzup.ctbupbit.provider.dto.Account;
import com.zzup.ctbupbit.trx.TrxContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TradingController {

    private UpbitService upbitService;
    private UpbitRestApiCaller api;
    private PolicyContainer policy;
    private TrxContainer trx;

    TradingController(UpbitService upbitService,
                      UpbitRestApiCaller api,
                      PolicyContainer policy,
                      TrxContainer trx) {
        this.upbitService = upbitService;
        this.api = api;
        this.policy = policy;
        this.trx = trx;
    }

    @GetMapping("/hello")
    public String hello() {
        return new String("Hello!!");
    }

    @GetMapping("/urgency")
    public void urgency() {
        trx.urgencyTrx();
    }

    @GetMapping("/account")
    public List<Account> getAccountList() {
        return api.getAccountList();
    }

    /*
    @GetMapping("/test")
    public void test() {
        trx.changeTrx();
    }

    @GetMapping("/sell")
    public void sell() throws UnsupportedEncodingException, NoSuchAlgorithmException, ParseException {
        upbitService.sell();
    }

    @GetMapping("/ticker")
    public List<Ticker> getTickerList() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return api.getTickerList("KRW-BTC,KRW-ETH,KRW-BCH,KRW-BSV,KRW-LTC,KRW-NEO,KRW-DOT,KRW-BTG,KRW-REP,KRW-LINK,KRW-BCHA,KRW-ATOM,KRW-ETC,KRW-WAVES,KRW-GAS,KRW-QTUM,KRW-OMG,KRW-TON,KRW-EOS,KRW-SRM,KRW-SBD,KRW-XTZ,KRW-KAVA,KRW-CBK,KRW-AQT,KRW-LSK,KRW-THETA,KRW-SXP,KRW-KNC,KRW-ICX,KRW-ZRX,KRW-PCI,KRW-KMD,KRW-ARK,KRW-IOTA,KRW-ADA,KRW-STORJ,KRW-ADX,KRW-GRS,KRW-XRP");
    }

    @GetMapping("/min")
    public List<CandleMinute> getMinuteCandleList() throws IOException, NoSuchAlgorithmException {
        return api.getMinuteCandleList("KRW-BTC",15, 15);
    }

    @GetMapping("/show")
    public List<OrderBook> showOrderBook() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return api.showOrderBook("KRW-BTC,KRW-ETH");
    }

    @GetMapping("/chance")
    public OrderChance getOrderChance() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return api.getOrdersChance("KRW-BTC");
    }
     */
}
