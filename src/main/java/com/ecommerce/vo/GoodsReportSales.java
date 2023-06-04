package com.ecommerce.vo;

import java.util.List;

import lombok.Builder;
import lombok.Data;

//一定要使用interfae @Query那邊的別名xxx有什麼 這邊就需要getxxx
public interface GoodsReportSales {
		
	long getOrderID();//訂單編號
	String getOrderDate();//購買日期
	String getCustomerName();//顧客姓名
	String getGoodsID();//商品編號
	String getGoodsName();//商品名稱
	int getGoodsBuyPrice();//商品價格
	int getBuyQuantity();//購買數量
	int getQuantity();//商品庫存
	int getOrderCount();//符合條件的總筆數(尚未分頁之前的)

}
