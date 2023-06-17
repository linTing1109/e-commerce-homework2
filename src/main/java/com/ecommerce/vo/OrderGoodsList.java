package com.ecommerce.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class OrderGoodsList { //訂單商品列表
	
	private long goodsID;//商品編號
	private String goodsName;//商品名稱
	private String description;//描述
	private int price;//價錢
	private int buyRealQuantity;//實際購買數量
	private String imageName;//圖片檔名

}
