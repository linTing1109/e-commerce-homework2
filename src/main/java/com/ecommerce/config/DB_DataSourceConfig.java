package com.ecommerce.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration //SpringBoot專案 標記類為配置類
public class DB_DataSourceConfig {

   @Primary //設置主要使用的資料庫
   @Bean(name = "oracle")
   /*
    * @ConfigurationProperties 將配置屬性映射到Java對象
    * prefix 設定為 springboot.datasource.oracle (這個可以看application.yml檔案) 
    */
   @ConfigurationProperties(prefix = "springboot.datasource.oracle")
   public DataSource oracleDataSource() {
      return DataSourceBuilder.create().build();
   }
   

}
