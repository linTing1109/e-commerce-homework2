package com.ecommerce.vo;

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
