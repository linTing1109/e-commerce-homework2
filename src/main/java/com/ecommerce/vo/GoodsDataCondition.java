package com.ecommerce.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class GoodsDataCondition {
	private Integer goodsID;
	private String goodsName;
	private Integer startPrice;//startPrice<=商品價錢
	private Integer endPrice;//商品價錢<=endPrice
//	private String priceSort;
	private Integer quantity;//庫存數量低於多少
	private String status;
	private String sort;//ASC/DESC
	private String orderByItem;//排序方式
}
