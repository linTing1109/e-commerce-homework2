package com.ecommerce.vo;

import java.util.List;

import com.ecommerce.entity.BeverageOrder;

import lombok.Data;

@Data
public class GoodsReportSalesInfo {
	private List<GoodsReportSales> goodsReportSalesList;
	private GenericPageable genericPageable;
	
}
