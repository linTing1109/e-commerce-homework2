package com.ecommerce.vo;

import java.util.List;
import lombok.Data;

@Data
public class GoodsOrderVo {

	private long goodsID;
	private String goodsName;
	private String description;	
	private int price;
	private int quantity;
	private String imageName;
	private String status;
	private List<OrderVo> orderVos;
	
}
