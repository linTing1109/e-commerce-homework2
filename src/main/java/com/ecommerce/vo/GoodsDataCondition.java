package com.ecommerce.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class GoodsDataCondition { //商品資料查詢
	private Integer goodsID; //商品編號
	private String goodsName; //商品名稱
	private Integer startPrice;//startPrice<=商品價錢
	private Integer endPrice;//商品價錢<=endPrice
//	private String priceSort; //統一改為orderByItem(可變動的)
	private Integer quantity;//庫存數量低於多少
	private String status; //狀態 上下架
	private String sort;//ASC/DESC
	private String orderByItem;//排序項目
}
