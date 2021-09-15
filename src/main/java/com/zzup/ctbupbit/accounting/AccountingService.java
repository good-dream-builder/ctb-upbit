package com.zzup.ctbupbit.accounting;

import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class AccountingService {
    private AccountBookRepository accountBookRepository;

    AccountingService(AccountBookRepository accountBookRepository) {
        this.accountBookRepository = accountBookRepository;
    }

    public AccountBook getLastAccountBookByCoin(String coin) {
        return accountBookRepository.findFirstByCoinOrderByDateTimeDesc(coin);
    }

    public List<AccountBook> getLast3AccountBookListByCoin(String coin) {
        return accountBookRepository.findTop3ByCoinOrderByIdDesc(coin);
    }

    public List<AccountBook> getLast5AccountBookListByCoin(String coin) {
        return accountBookRepository.findTop5ByCoinOrderByIdDesc(coin);
    }

    public Double getProfit(String from, String to) throws ParseException {
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = transFormat.parse(from);
        Date toDate = transFormat.parse(to);

        Double sumOfProfit = accountBookRepository.getProfitBetweenDate(fromDate, toDate);
        return sumOfProfit;
    }

    @Deprecated
    public Double getProfitOld(String from, String to) throws ParseException {
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = transFormat.parse(from);
        Date toDate = transFormat.parse(to);

        List<AccountBook> accountBookList = accountBookRepository.findAllByDateTimeBetween(fromDate, toDate);
        Double profit = 0.0;
        for (AccountBook el : accountBookList) {
            profit += el.getProfit();
        }
        return profit;
    }

    public Double getProfit() {
        List<AccountBook> accountBookList = accountBookRepository.findAll();

        Double profit = 0.0;
        for (AccountBook el : accountBookList) {
            profit += el.getProfit();
        }

        return profit;
    }

    public List<AccountBook> getAllAccountBookList() throws ParseException {
        List<AccountBook> accountBookList = accountBookRepository.findAll();
        return accountBookList;
    }

    public List<AccountBook> getAllAccountBookListByCoin(String market) throws ParseException {
        List<AccountBook> accountBookList = accountBookRepository.findAllByCoin(market);
        return accountBookList;
    }

    public void updateAccountBook(AccountBook accountBook) {
        accountBookRepository.save(accountBook);
    }
}
