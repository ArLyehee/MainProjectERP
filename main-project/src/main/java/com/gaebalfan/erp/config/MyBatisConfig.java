package com.gaebalfan.erp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
// @MapperScan 제거 - ErpApplication.java에 이미 있음
public class MyBatisConfig {
}
