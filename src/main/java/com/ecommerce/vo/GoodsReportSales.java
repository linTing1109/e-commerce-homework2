package com.ecommerce.vo;


//給BeverageOrderDao 回傳 全部訂單 & 個人訂單 使用  
//JPQL的自定義資料反射欄位對應 建立介面定義所回傳的資料物件 並透過getXXX方法對應所查詢的欄位別名
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
