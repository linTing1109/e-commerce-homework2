package com.ecommerce.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class GoodsSalesReportCondition { //銷售報表查詢條件
	private String startDate; //開始時間
	private String endDate; //結束時間
	private String sort; //ASC/DESC
	private String orderByItem; //排序項目
}
