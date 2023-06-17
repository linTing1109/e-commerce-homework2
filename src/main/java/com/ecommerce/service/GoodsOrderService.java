package com.ecommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dao.BeverageGoodsDao;
import com.ecommerce.dao.BeverageOrderDao;
import com.ecommerce.entity.BeverageGoods;
import com.ecommerce.entity.BeverageOrder;
import com.ecommerce.vo.GoodsOrderVo;
import com.ecommerce.vo.OrderVo;


@Service
public class GoodsOrderService {
//	private static Logger logger = LoggerFactory.getLogger(GoodsOrderService.class);
	
	@Autowired
	private BeverageGoodsDao beverageGoodsDao;
	@Autowired
	private BeverageOrderDao beverageOrderDao;
	
	@Transactional
	public BeverageGoods createGoodsOrder(GoodsOrderVo goodsOrderVo) {
		BeverageGoods beverageGoods = BeverageGoods.builder()
				.goodsID(goodsOrderVo.getGoodsID())
				.goodsName(goodsOrderVo.getGoodsName())
				.description(goodsOrderVo.getDescription())
				.goodsPrice(goodsOrderVo.getPrice())
				.goodsQuantity(goodsOrderVo.getQuantity())
				.goodsImageName(goodsOrderVo.getImageName())
				.status(goodsOrderVo.getStatus()).build();
		//先讓goodID產生出來再去order那邊
		beverageGoods=beverageGoodsDao.save(beverageGoods);
		List<BeverageOrder> beverageOrders = new ArrayList<>();
		for(OrderVo orderVo : goodsOrderVo.getOrderVos()){
			BeverageOrder beverageOrder = BeverageOrder.builder()
					.orderID(orderVo.getOrderID())
					.orderDate(orderVo.getOrderDate())
					.customerID(orderVo.getCustomerID())
					.goodsID(beverageGoods.getGoodsID())
					.goodsByPrice(orderVo.getGoodsBuyPrice())
					.buyQuantity(orderVo.getBuyQuantity()).build();
			beverageOrders.add(beverageOrder);	
		}
		beverageGoods.setBeverageOrders(beverageOrders);
		
		// 透過儲存「一」的那方Geography CascadeType.ALL屬性，就可以連同「多」的那方StoreInfo一併儲存!
//		beverageGoods = beverageGoodsDao.save(beverageGoods);
		
		return beverageGoodsDao.save(beverageGoods);
	}
	
	
	@Transactional
	public BeverageGoods updateGoodsOrder(GoodsOrderVo goodsOrderVo) {
		//DB Good開一個來放
		BeverageGoods dbBeverageGoods = null;
		//若輸入的goodID 有在db內將資料塞入dbBeverageGoods
		Optional<BeverageGoods> optBeverageGoods = beverageGoodsDao.findById(goodsOrderVo.getGoodsID());
		if(optBeverageGoods.isPresent()){
			//先更新good內容
			dbBeverageGoods = optBeverageGoods.get();
			dbBeverageGoods.setGoodsName(goodsOrderVo.getGoodsName());
			dbBeverageGoods.setGoodsPrice(goodsOrderVo.getPrice());
			//放入修改後 新的order
			List<BeverageOrder> newBeverageOrders = new ArrayList<>();
			//得到原本db內的order
			List<BeverageOrder> dbBeverageOrders = dbBeverageGoods.getBeverageOrders();
			for(OrderVo orderVo : goodsOrderVo.getOrderVos()){
				BeverageOrder beverageOrder = BeverageOrder.builder().orderID(orderVo.getOrderID()).build();
//				storeInfo = (dbStoreInfos.contains(storeInfo)) ? dbStoreInfos.get(dbStoreInfos.indexOf(storeInfo)) : storeInfo;
				
				//如果已經在原先內的話取得原先的值
				if(dbBeverageOrders.contains(beverageOrder)) {
					beverageOrder = dbBeverageOrders.get(dbBeverageOrders.indexOf(beverageOrder));
				}
				//order要修改的部分
				if(orderVo.getOrderDate()!=null) beverageOrder.setOrderDate(orderVo.getOrderDate());
				if(orderVo.getBuyQuantity()!=0) beverageOrder.setBuyQuantity(orderVo.getBuyQuantity());
				if(orderVo.getCustomerID()!=null) beverageOrder.setCustomerID(orderVo.getCustomerID());
				if(orderVo.getGoodsBuyPrice()!=0) beverageOrder.setGoodsByPrice(orderVo.getGoodsBuyPrice());
				beverageOrder.setGoodsID(goodsOrderVo.getGoodsID());
				beverageOrder=beverageOrderDao.save(beverageOrder);
				newBeverageOrders.add(beverageOrder);
			}
			dbBeverageOrders.clear();
			dbBeverageOrders.addAll(newBeverageOrders);
		}
		
		return dbBeverageGoods;
	}
	
	
}
