package com.ecommerce.vo;

import java.util.List;

import com.ecommerce.entity.BeverageGoods;

import lombok.Data;

@Data
public class GoodsDataInfo {
	private List<BeverageGoods> goodsDatas;
	private GenericPageable genericPageable;
}
