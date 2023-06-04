package com.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.entity.BeverageGoods;
import com.ecommerce.service.GoodsOrderService;
import com.ecommerce.vo.GoodsOrderVo;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/ecommerce/GoodsOrderController")
public class GoodsOrderController {
	//這支Controller只是 一對多相關練習 跟購物車功能無關:這邊要用要去把BeverageOrder的GOODS_ID那邊註解打開
	// 現在主要以e-commerce功能為主 打開GOODS_ID的註解在成立訂單的時候 會有oder插入客戶訂單ID失敗 
	@Autowired
	private GoodsOrderService goodsOrderService;
	
	@ApiOperation("新增商品訂單(一對多練習)")
	@PostMapping(value = "/createGoodsOrder")
	public ResponseEntity<BeverageGoods> createGoodsOrder(@RequestBody GoodsOrderVo goodsOrderVo) {
		// 新增「一筆商品」(116)同時新增「多筆訂單」313、314、315
		/*
			{
			  "goodsName": "iPhone 12 Pro",
			  "description": "超瓷晶盾面板 霧面質感玻璃機背 不鏽鋼設計",
			  "imageName": "iPhone12.jpg",
			  "price": 32000,
			  "quantity": 10,
			  "status": "1",
			  "orderVos": [
			    {
			      "orderDate": "2023-03-01T00:00:00",
			      "customerID": "A124243295",
			      "buyQuantity": 1,
			      "goodsBuyPrice": 32000
			    },
			    {
			      "orderDate": "2023-03-02T00:00:00",
			      "customerID": "D201663865",
			      "buyQuantity": 1,
			      "goodsBuyPrice": 32000
			    },
			    {
			      "orderDate": "2023-03-03T00:00:00",
			      "customerID": "J213664153",
			      "buyQuantity": 1,
			      "goodsBuyPrice": 32000
			    }
			  ]
			}
		 */
		BeverageGoods beverageGoods = goodsOrderService.createGoodsOrder(goodsOrderVo);
		
		return ResponseEntity.ok(beverageGoods);
	}
	
	@ApiOperation("更新商品訂單(一對多練習)")
	@PatchMapping(value = "/updateGoodsOrder")
	public ResponseEntity<BeverageGoods> updateGoodsOrder(@RequestBody GoodsOrderVo goodsOrderVo) {
		// 更新「一筆商品」同時更新「多筆訂單」
		/*
`			前端只傳入須要更新的部份欄位,未傳入的欄位不更新!
			UPDATE * 1 筆商品(116)
			UPDATE * 2 筆訂單(313、314)
			INSERT * 2 筆訂單(316、317)
			DELETE * 1 筆訂單(315)
			{
			  "goodsID": 116,
			  "goodsName": "iPhone 12 Pro(256GB)",
			  "price": 36666,
			  "orderVos": [
			    {
			      "orderID": 313,
			      "orderDate": "2023-04-01T00:00:00"
			    },
			    {
			      "orderID": 314,
			      "buyQuantity": 3
			    },
			    {
			      "orderDate": "2023-05-01T00:00:00",
			      "customerID": "F126873254",
			      "buyQuantity": 1,
			      "goodsBuyPrice": 32000
			    },
			    {
			      "orderDate": "2023-05-02T00:00:00",
			      "customerID": "G436565447",
			      "buyQuantity": 1,
			      "goodsBuyPrice": 32000
			    }
			  ]
			}
			
		 */
		BeverageGoods beverageGoods = goodsOrderService.updateGoodsOrder(goodsOrderVo);
		
		return ResponseEntity.ok(beverageGoods);		
	}
	
}
