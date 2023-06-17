package com.ecommerce.vo;

import java.util.List;

import lombok.Data;

@Data
public class CheckoutCompleteInfo { //結帳完成資訊
	private OrderCustomer customer; //顧客資料
	private List<OrderGoodsList> orderGoodsList; //訂單商品列表
}
