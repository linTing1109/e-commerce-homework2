package com.ecommerce.vo;

//給BeverageOrderDao 回傳 後臺銷售報表 使用 
//JPQL的自定義資料反射欄位對應 建立介面定義所回傳的資料物件 並透過getXXX方法對應所查詢的欄位別名
public interface GoodsReport {
	
	Long getGoodsID();//商品編號
	String getGoodsName();//商品名稱
	int getTotal();//購買總數量
}
