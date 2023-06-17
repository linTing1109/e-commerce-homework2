package com.ecommerce.vo;

import java.util.List;

import lombok.Data;

@Data
public class GoodsReportSalesInfo {
	private List<GoodsReportSales> goodsReportSalesList;
	private GenericPageable genericPageable;
	
}
