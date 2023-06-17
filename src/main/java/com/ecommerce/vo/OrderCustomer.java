package com.ecommerce.vo;

import lombok.Data;

@Data
public class OrderCustomer { //顧客資料
	private String cusName; //顧客姓名
	private String homeNumber; //住家電話
	private String mobileNumber; //手機
	private String orderAddr; //訂購地址
}
