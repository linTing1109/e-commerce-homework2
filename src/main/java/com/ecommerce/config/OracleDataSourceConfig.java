package com.ecommerce.config;

import java.util.Map;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement; 

@Configuration //SpringBoot專案 標記類為配置類
@EnableTransactionManagement //資料庫教義管理
@EnableJpaRepositories( //啟用Spring Data JPA 功能
	//實體管理工廠 可自行控制Transaction交易管理	
	entityManagerFactoryRef = "oracleEntityManagerFactory",
	//可在Service method上標註使用@Transactional
	transactionManagerRef = "oracleTransactionManager", 
	//設置 @Repository DAO class 的 Package 路徑
	basePackages = { "com.ecommerce.dao" }
)
public class OracleDataSourceConfig { 

   @Autowired
   @Qualifier("oracle") //指定要注入的DataSource Bean 名稱(在DB_DataSourceConfig那邊設定的名稱)
   private DataSource dataSource; 

   @Autowired
   private HibernateProperties hibernateProperties; 

   @Autowired
   private JpaProperties jpaProperties; 

   @Primary
   @Bean(name = "oracleEntityManager") //CriteriaQuery(注入EntityManager以建立並取得CriteriaBuilder進行進階動態查詢標準查詢)
   public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
      return entityManagerFactory(builder).getObject().createEntityManager();
   } 

   @Primary
   @Bean(name = "oracleEntityManagerFactory") //可自行控制Transaction交易管理
   public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
      return builder.dataSource(dataSource)
    		  .properties(getVendorProperties())
    		  .packages("com.ecommerce.entity").build();
   } 

   private Map<String, Object> getVendorProperties() {
      return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
   } 

   @Primary
   @Bean(name = "oracleTransactionManager") //交易管理
   public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
      return new JpaTransactionManager(entityManagerFactory(builder).getObject());
   }

}
