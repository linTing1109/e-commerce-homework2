package com.ecommerce.vo;

import java.util.List;

import lombok.Data;

@Data
public class CheckoutCompleteInfo {
	private OrderCustomer customer;
	private List<OrderGoodsList> orderGoodsList;
}
