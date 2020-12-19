package com.ths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScheduleApplication {

    /********************************************************************************
     * 运行前先创建好数据表，语句在：
     *
     * sql/东方财富-资金流向-建表语句/stock_dfcf_fund_flow_info.sql
     * sql/同花顺-板块信息-建表语句/stock_ths_gn_info.sql
     *
     * ********************************************************************************
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ScheduleApplication.class, args);
    }

}
