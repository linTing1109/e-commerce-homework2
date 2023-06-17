package com.ecommerce.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.entity.BeverageGoods;

@Repository //標記資料庫交易持久層DAO元件
public interface BeverageGoodsDao extends JpaRepository<BeverageGoods, Long>{
	//繼承JpaRepository 就可以寫 語法關鍵字 及 實體屬性欄位 所組合的抽象方法
	
	//條件where:goodsID 並且不為空 排序方式:從大到小
	List<BeverageGoods> findByGoodsIDIsNotNullOrderByGoodsIDDesc();
	
	//條件where:goodsID
	BeverageGoods findByGoodsID(Long goodsID);
}
