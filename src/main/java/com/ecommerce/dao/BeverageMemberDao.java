package com.ecommerce.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.entity.BeverageMember;

@Repository //標記資料庫交易持久層DAO元件
public interface BeverageMemberDao extends JpaRepository<BeverageMember, String>{
	//繼承JpaRepository 就可以寫 語法關鍵字 及 實體屬性欄位 所組合的抽象方法
	
	//條件where:傳入的會員帳號 跟傳入會員密碼  -->符合的會員資料
	BeverageMember findByIdentificationNoAndPassword(String identificationNo, String password);
	
	//條件where:傳入的會員帳號 -->符合的會員資料
	BeverageMember findByIdentificationNo(String identificationNo);
}
