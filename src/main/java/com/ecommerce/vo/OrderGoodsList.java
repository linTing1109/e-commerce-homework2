package com.ecommerce.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class OrderGoodsList {
	
	private long goodsID;
	private String goodsName;
	private String description;
	private int price;
	private int buyRealQuantity;//實際購買數量
	private String imageName;

}
