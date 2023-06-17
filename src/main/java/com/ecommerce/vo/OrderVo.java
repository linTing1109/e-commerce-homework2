package com.ecommerce.vo;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class OrderVo {

	private long orderID;
	private LocalDateTime orderDate;
	private String customerID;
	private long goodsID;
	private long goodsBuyPrice;
	private long buyQuantity;
	
}
