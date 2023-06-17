package com.ecommerce.vo;

//給BeverageOrderDao 回傳 前臺熱銷表 使用  
//JPQL的自定義資料反射欄位對應 建立介面定義所回傳的資料物件 並透過getXXX方法對應所查詢的欄位別名
public interface SellWellGoods {
	Long getGoodsID();//商品編號
	int getTotal();//購買總數量
	String getGoodsName();//商品名稱
	String getDescription();//商品描述
	int getPrice();//商品價錢
	int getQuantity();//商品餘額
	String getImageName();//商品圖片名稱
	String getStatus();//商品上架狀態
}
