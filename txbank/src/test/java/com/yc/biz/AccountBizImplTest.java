package com.yc.biz;

import com.yc.bean.Account;
import com.yc.configs.Config;
import com.yc.configs.DataSourceConfig;
import junit.framework.TestCase;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, DataSourceConfig.class})
@Log4j2
public class AccountBizImplTest extends TestCase {

    @Autowired
    private AccountBiz accountBiz;

    @Test
    public void testOpenAccount() {
        Account a = accountBiz.openAccount(3);
        Assert.assertNotNull(a);
        log.info(a);
    }

    @Test
    public void testFindAccount() {
        Account a = accountBiz.findAccount(1);
        Assert.assertNotNull(a);
        log.info(a);
    }

    @Test
    public void testAddAccount() {
        Account a = accountBiz.openAccount(100);
        Assert.assertNotNull(a);
        log.info(a);
    }

    @Test
    public void testDeposite() {
        Account a = accountBiz.deposite(1,1);
        Assert.assertNotNull(a);
        log.info(a);
    }

    @Test
    public void testWithdraw() {
        Account a = accountBiz.withdraw(1,10000);
        Assert.assertNotNull(a);
        log.info(a);
    }

    @Test
    public void testTransfer() {
        Account a = accountBiz.transfer(1,2,2);
        Assert.assertNotNull(a);
        log.info(a);
    }
}