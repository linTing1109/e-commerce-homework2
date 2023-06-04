package com.ecommerce.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DB_DataSourceConfig {

   @Primary
   @Bean(name = "oracle")
   @ConfigurationProperties(prefix = "springboot.datasource.oracle")
   public DataSource oracleDataSource() {
      return DataSourceBuilder.create().build();
   }
   

}
