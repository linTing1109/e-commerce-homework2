package com.ecommerce.vo;

import java.util.List;

import com.ecommerce.entity.BeverageGoods;
import com.ecommerce.entity.BeverageOrder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class GoodsSalesReportCondition {
	private String startDate;
	private String endDate;
	private String sort;
	private String orderByItem;
}
