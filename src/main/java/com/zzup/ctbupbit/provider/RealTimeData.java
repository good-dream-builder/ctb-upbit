package com.zzup.ctbupbit.provider;

import java.io.Serializable;
import java.util.Date;


public class RealTimeData implements Serializable {
    private String type;
    private String code;
    private Double opening_price;
    private Double high_price;
    private Double low_price;
    private Double trade_price;
    private Double prev_closing_price;
    private Double acc_trade_price;
    private String change;
    private Double change_price;
    private Double signed_change_price;
    private Double change_rate;
    private Double signed_change_rate;
    private String ask_bid;
    private Double trade_volume;
    private Double acc_trade_volume;
    private String trade_date;
    private String trade_time;
    private Date trade_timestamp;
    private Double acc_ask_volume;
    private Double acc_bid_volume;
    private Double highest_52_week_price;
    private String highest_52_week_date;
    private Double lowest_52_week_price;
    private String lowest_52_week_date;
    private String trade_status;
    private String market_state;
    private String market_state_for_ios;
    private Boolean is_trading_suspended;
    private String delisting_date;
    private String market_warning;
    private Date timestamp;
    private Double acc_trade_price_24h;
    private Double acc_trade_volume_24h;
    private String stream_type;

    @Override
    public String toString() {
        return "RealTimeData{" +
                "trade_price=" + trade_price +
                ", change='" + change + '\'' +
                ", change_price=" + change_price +
                ", change_rate=" + change_rate +
                ", trade_volume=" + trade_volume +
                ", trade_time='" + trade_time + '\'' +
                '}';
    }

    public RealTimeData() {
    }

    public RealTimeData(String type, String code, Double opening_price, Double high_price, Double low_price, Double trade_price, Double prev_closing_price, Double acc_trade_price, String change, Double change_price, Double signed_change_price, Double change_rate, Double signed_change_rate, String ask_bid, Double trade_volume, Double acc_trade_volume, String trade_date, String trade_time, Date trade_timestamp, Double acc_ask_volume, Double acc_bid_volume, Double highest_52_week_price, String highest_52_week_date, Double lowest_52_week_price, String lowest_52_week_date, String trade_status, String market_state, String market_state_for_ios, Boolean is_trading_suspended, String delisting_date, String market_warning, Date timestamp, Double acc_trade_price_24h, Double acc_trade_volume_24h, String stream_type) {
        this.type = type;
        this.code = code;
        this.opening_price = opening_price;
        this.high_price = high_price;
        this.low_price = low_price;
        this.trade_price = trade_price;
        this.prev_closing_price = prev_closing_price;
        this.acc_trade_price = acc_trade_price;
        this.change = change;
        this.change_price = change_price;
        this.signed_change_price = signed_change_price;
        this.change_rate = change_rate;
        this.signed_change_rate = signed_change_rate;
        this.ask_bid = ask_bid;
        this.trade_volume = trade_volume;
        this.acc_trade_volume = acc_trade_volume;
        this.trade_date = trade_date;
        this.trade_time = trade_time;
        this.trade_timestamp = trade_timestamp;
        this.acc_ask_volume = acc_ask_volume;
        this.acc_bid_volume = acc_bid_volume;
        this.highest_52_week_price = highest_52_week_price;
        this.highest_52_week_date = highest_52_week_date;
        this.lowest_52_week_price = lowest_52_week_price;
        this.lowest_52_week_date = lowest_52_week_date;
        this.trade_status = trade_status;
        this.market_state = market_state;
        this.market_state_for_ios = market_state_for_ios;
        this.is_trading_suspended = is_trading_suspended;
        this.delisting_date = delisting_date;
        this.market_warning = market_warning;
        this.timestamp = timestamp;
        this.acc_trade_price_24h = acc_trade_price_24h;
        this.acc_trade_volume_24h = acc_trade_volume_24h;
        this.stream_type = stream_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getOpening_price() {
        return opening_price;
    }

    public void setOpening_price(Double opening_price) {
        this.opening_price = opening_price;
    }

    public Double getHigh_price() {
        return high_price;
    }

    public void setHigh_price(Double high_price) {
        this.high_price = high_price;
    }

    public Double getLow_price() {
        return low_price;
    }

    public void setLow_price(Double low_price) {
        this.low_price = low_price;
    }

    public Double getTrade_price() {
        return trade_price;
    }

    public void setTrade_price(Double trade_price) {
        this.trade_price = trade_price;
    }

    public Double getPrev_closing_price() {
        return prev_closing_price;
    }

    public void setPrev_closing_price(Double prev_closing_price) {
        this.prev_closing_price = prev_closing_price;
    }

    public Double getAcc_trade_price() {
        return acc_trade_price;
    }

    public void setAcc_trade_price(Double acc_trade_price) {
        this.acc_trade_price = acc_trade_price;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public Double getChange_price() {
        return change_price;
    }

    public void setChange_price(Double change_price) {
        this.change_price = change_price;
    }

    public Double getSigned_change_price() {
        return signed_change_price;
    }

    public void setSigned_change_price(Double signed_change_price) {
        this.signed_change_price = signed_change_price;
    }

    public Double getChange_rate() {
        return change_rate;
    }

    public void setChange_rate(Double change_rate) {
        this.change_rate = change_rate;
    }

    public Double getSigned_change_rate() {
        return signed_change_rate;
    }

    public void setSigned_change_rate(Double signed_change_rate) {
        this.signed_change_rate = signed_change_rate;
    }

    public String getAsk_bid() {
        return ask_bid;
    }

    public void setAsk_bid(String ask_bid) {
        this.ask_bid = ask_bid;
    }

    public Double getTrade_volume() {
        return trade_volume;
    }

    public void setTrade_volume(Double trade_volume) {
        this.trade_volume = trade_volume;
    }

    public Double getAcc_trade_volume() {
        return acc_trade_volume;
    }

    public void setAcc_trade_volume(Double acc_trade_volume) {
        this.acc_trade_volume = acc_trade_volume;
    }

    public String getTrade_date() {
        return trade_date;
    }

    public void setTrade_date(String trade_date) {
        this.trade_date = trade_date;
    }

    public String getTrade_time() {
        return trade_time;
    }

    public void setTrade_time(String trade_time) {
        this.trade_time = trade_time;
    }

    public Date getTrade_timestamp() {
        return trade_timestamp;
    }

    public void setTrade_timestamp(Date trade_timestamp) {
        this.trade_timestamp = trade_timestamp;
    }

    public Double getAcc_ask_volume() {
        return acc_ask_volume;
    }

    public void setAcc_ask_volume(Double acc_ask_volume) {
        this.acc_ask_volume = acc_ask_volume;
    }

    public Double getAcc_bid_volume() {
        return acc_bid_volume;
    }

    public void setAcc_bid_volume(Double acc_bid_volume) {
        this.acc_bid_volume = acc_bid_volume;
    }

    public Double getHighest_52_week_price() {
        return highest_52_week_price;
    }

    public void setHighest_52_week_price(Double highest_52_week_price) {
        this.highest_52_week_price = highest_52_week_price;
    }

    public String getHighest_52_week_date() {
        return highest_52_week_date;
    }

    public void setHighest_52_week_date(String highest_52_week_date) {
        this.highest_52_week_date = highest_52_week_date;
    }

    public Double getLowest_52_week_price() {
        return lowest_52_week_price;
    }

    public void setLowest_52_week_price(Double lowest_52_week_price) {
        this.lowest_52_week_price = lowest_52_week_price;
    }

    public String getLowest_52_week_date() {
        return lowest_52_week_date;
    }

    public void setLowest_52_week_date(String lowest_52_week_date) {
        this.lowest_52_week_date = lowest_52_week_date;
    }

    public String getTrade_status() {
        return trade_status;
    }

    public void setTrade_status(String trade_status) {
        this.trade_status = trade_status;
    }

    public String getMarket_state() {
        return market_state;
    }

    public void setMarket_state(String market_state) {
        this.market_state = market_state;
    }

    public String getMarket_state_for_ios() {
        return market_state_for_ios;
    }

    public void setMarket_state_for_ios(String market_state_for_ios) {
        this.market_state_for_ios = market_state_for_ios;
    }

    public Boolean getIs_trading_suspended() {
        return is_trading_suspended;
    }

    public void setIs_trading_suspended(Boolean is_trading_suspended) {
        this.is_trading_suspended = is_trading_suspended;
    }

    public String getDelisting_date() {
        return delisting_date;
    }

    public void setDelisting_date(String delisting_date) {
        this.delisting_date = delisting_date;
    }

    public String getMarket_warning() {
        return market_warning;
    }

    public void setMarket_warning(String market_warning) {
        this.market_warning = market_warning;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Double getAcc_trade_price_24h() {
        return acc_trade_price_24h;
    }

    public void setAcc_trade_price_24h(Double acc_trade_price_24h) {
        this.acc_trade_price_24h = acc_trade_price_24h;
    }

    public Double getAcc_trade_volume_24h() {
        return acc_trade_volume_24h;
    }

    public void setAcc_trade_volume_24h(Double acc_trade_volume_24h) {
        this.acc_trade_volume_24h = acc_trade_volume_24h;
    }

    public String getStream_type() {
        return stream_type;
    }

    public void setStream_type(String stream_type) {
        this.stream_type = stream_type;
    }
}
