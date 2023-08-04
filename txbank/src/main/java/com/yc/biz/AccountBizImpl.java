package com.yc.biz;

import com.yc.bean.Account;
import com.yc.bean.OpRecord;
import com.yc.bean.OpType;
import com.yc.dao.AccountDao;
import com.yc.dao.OpRecordDao;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@Transactional
public class AccountBizImpl implements AccountBiz{
    @Override
    public void addAccount(int accountid, double money){
        System.out.println("添加账户..."+accountid);
    }

    @Autowired
    private AccountDao accountDao;
    @Autowired
    private OpRecordDao opRecordDao;

    @Override
    public Account findAccount(int accountId) {
        return this.accountDao.findById(accountId);
    }

    @Override
    public Account openAccount(double money) {
        int accountid = this.accountDao.insert(money);
        OpRecord opRecord = new OpRecord();
        opRecord.setAccountid(accountid);
        opRecord.setOpmoney(money);
        opRecord.setOptype(OpType.DEPOSITE);
        this.opRecordDao.insertOpRecord(opRecord);
        Account a = new Account();
        a.setAccountid(accountid);
        a.setMoney(money);
        return a;
    }

    @Override
    public Account deposite(int accountid, double money) {
        return this.deposite(accountid, money,null);
    }

    @Override
    public Account deposite(int accountid, double money, Integer transferId) {
        Account a = null;
        try{
            a = this.accountDao.findById(accountid);
        }catch (RuntimeException re){
            log.error(re.getMessage());
            throw new RuntimeException("查无此账户"+ accountid +",无法完成存取款操作");
        }
        a.setMoney(a.getMoney()+money);
        this.accountDao.update(accountid, a.getMoney());

        OpRecord opRecord = new OpRecord();
        opRecord.setAccountid(accountid);
        opRecord.setOpmoney(money);
        if (transferId!=null){
            opRecord.setOptype(OpType.TRANSFER);
            opRecord.setTransferid(transferId);
        }else {
            opRecord.setOptype(OpType.WITHDRAW);
        }
        this.opRecordDao.insertOpRecord(opRecord);
        return a;
    }

    @Override
    public Account withdraw(int accountid, double money) {
        return this.withdraw(accountid,money,null);
    }

    @Override
    public Account withdraw(int accountid, double money, Integer transferId) {
        Account a = null;
        try{
            a = this.accountDao.findById(accountid);
        }catch (RuntimeException re){
            log.error(re.getMessage());
            throw new RuntimeException("查无此账户"+ accountid +",无法完成存取款操作");
        }
        a.setMoney(a.getMoney()-money);
        OpRecord opRecord = new OpRecord();
        opRecord.setAccountid(accountid);
        opRecord.setOpmoney(money);
        if (transferId!=null){
            opRecord.setOptype(OpType.TRANSFER);
            opRecord.setTransferid(transferId);
        }else {
            opRecord.setOptype(OpType.DEPOSITE);
        }

        this.opRecordDao.insertOpRecord(opRecord);
        this.accountDao.update(accountid, a.getMoney());
        return a;
    }

    @Override
    public Account transfer(int accountId, double money, int toAccountId) {
        this.deposite(toAccountId, money, accountId);
        Account a = this.withdraw(accountId, money, toAccountId);
        return a;
    }

}
