package com.yc.configs;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:db.properties")
@Log4j2
@EnableTransactionManagement

public class DataSourceConfig {
    @Value("root")
    private String mysqlname;
    @Value("123123")
    private String password;
    @Value("jdbc:mysql://localhost:3306/testbank?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone = GMT")
    private String url;
    @Value("com.mysql.cj.jdbc.Driver")
    private String driverclass;
    @Value("#{ T(Runtime).getRuntime().availableProcessors()*2 }")
    private int cpuCount;

    @Override
    public String toString() {
        return "DataSourceConfig{" +
                "mysqlname='" + mysqlname + '\'' +
                ", password='" + password + '\'' +
                ", url='" + url + '\'' +
                ", driverclass='" + driverclass + '\'' +
                ", cpuCount=" + cpuCount +
                '}';
    }

    public int getCpuCount() {
        return cpuCount;
    }

    public void setCpuCount(int cpuCount) {
        this.cpuCount = cpuCount;
    }

    public String getMysqlname() {
        return mysqlname;
    }

    public void setMysqlname(String mysqlname) {
        this.mysqlname = mysqlname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverclass() {
        return driverclass;
    }

    public void setDriverclass(String driverclass) {
        this.driverclass = driverclass;
    }

    @Bean
    public TransactionManager dataSourceTransactionManager(@Autowired @Qualifier(value = "dataSource") DataSource ds){
        DataSourceTransactionManager tx = new DataSourceTransactionManager();
        tx.setDataSource(ds);
        return tx;
    }

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverclass);
        dataSource.setUrl(url);
        dataSource.setUsername(mysqlname);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public DataSource dbcpDataSource(){
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverclass);
        dataSource.setUrl(url);
        dataSource.setUsername(mysqlname);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public DruidDataSource druidDataSource(){
        DruidDataSource dds = new DruidDataSource();
        dds.setUrl(url);
        dds.setUsername(mysqlname);
        dds.setPassword(password);
        dds.setDriverClassName(driverclass);

        log.info("配置druid的连接池大小:" + cpuCount);
        dds.setInitialSize(cpuCount);
        dds.setMaxActive(cpuCount*2);
        return dds;
    }


}
