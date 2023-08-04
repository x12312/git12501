package com.yc.biz;

import com.yc.bean.Account;

public interface AccountBiz {
    void addAccount(int accountid, double money);

    public Account openAccount(double money);

    public Account deposite(int accountid, double money);

    Account deposite(int accountid, double money, Integer transferId);

    public Account withdraw(int accountid, double money);

    public Account withdraw(int accountid, double money, Integer transferId);

    public Account transfer(int accountId, double money, int toAccountId);

    public Account findAccount(int accountId);

}
