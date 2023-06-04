package com.ecommerce.service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dao.BeverageGoodsDao;
import com.ecommerce.dao.BeverageOrderDao;
import com.ecommerce.dao.CriteriaQueryDao;
import com.ecommerce.entity.BeverageGoods;
import com.ecommerce.entity.BeverageOrder;
import com.ecommerce.vo.CheckoutCompleteInfo;
import com.ecommerce.vo.GenericPageable;
import com.ecommerce.vo.GoodsReport;
import com.ecommerce.vo.GoodsVo;
import com.ecommerce.vo.MemberInfo;
import com.ecommerce.vo.OrderCustomer;
import com.ecommerce.vo.OrderGoodsList;
import com.ecommerce.vo.ProductGoodsInfo;
import com.ecommerce.vo.SellWellGoods;

@Service
public class FrontendService {
	private static Logger logger = LoggerFactory.getLogger(FrontendService.class);
	
	@Resource(name="member")
	private MemberInfo sessionMemberInfo;
	
	@Resource(name = "sessionCartGoods") 
	private List<GoodsVo> cartGoods;
	
	@Autowired
	private CriteriaQueryDao criteriaQueryDao;
	
	@Autowired
	private BeverageGoodsDao beverageGoodsDao;
	
	@Autowired
	private BeverageOrderDao beverageOrderDao;
	
	public ProductGoodsInfo queryGoodsData(String searchKeyword, GenericPageable genericPageable) {
		ProductGoodsInfo productGoodsInfo=new ProductGoodsInfo();
		List<BeverageGoods> goodsDatas = criteriaQueryDao.queryGoodsKeyword(searchKeyword,genericPageable);//排序使用id的ASC
		
		//給容器讓Pagination可以塞值
		Set<Integer> number=new HashSet<>();
		genericPageable.setPagination(number);
		
		productGoodsInfo.setGoodsDatas(goodsDatas);
		productGoodsInfo.setGenericPageable(genericPageable);
		return productGoodsInfo;
	}
	@Transactional
	public CheckoutCompleteInfo checkoutGoods(OrderCustomer customer) {
		CheckoutCompleteInfo checkoutCompleteInfo=new CheckoutCompleteInfo();
		List<OrderGoodsList> orderGoodsAll=new ArrayList<>();
		
		//先將購物車同筆ID 計算在一起
		Map<GoodsVo,Integer> resultMap=new HashMap<>();  
		for(GoodsVo good:cartGoods) {
			int buyQuantity=1;//預計要購買的數量
			if(resultMap.containsKey(good)) {//檢查裡面是否已經有相同id
				int existingQuantity=resultMap.get(good);
				resultMap.put(good, existingQuantity+1);//每一筆當作買一個
			}else {
				resultMap.put(good, buyQuantity);
			}		
		}
		//實際開始比較庫存>=購買 符合:扣DB庫存&增加訂單
		for(GoodsVo goods:resultMap.keySet()) {
			int buyQuantity=resultMap.get(goods);//預計購買數量
			Optional<BeverageGoods> beverageGoodsOpt=beverageGoodsDao.findById(goods.getGoodsID());
			BeverageGoods beverageGoods = null;
			if(beverageGoodsOpt.isPresent()) {
				beverageGoods=beverageGoodsOpt.get();
				// 庫存是否大於預計購買 是:放入預計購買 		否:只放入剩餘庫存量
				buyQuantity=beverageGoods.getGoodsQuantity()>= buyQuantity ? buyQuantity:beverageGoods.getGoodsQuantity();
//				if(beverageGoods.getGoodsQuantity()>= buyQuantity) { //庫存>=預計購買才動作
					//將DB的實際庫存減少
					beverageGoods.setGoodsQuantity(beverageGoodsOpt.get().getGoodsQuantity()-buyQuantity);
					//增加DB的order訂單
					BeverageOrder creatOrder=BeverageOrder.builder()
							//date轉instant 再轉zoneDatetime(並且默認系統時區) 再轉為localdatetime
							.orderDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
							.customerID(sessionMemberInfo.getIdentificationNo())
							.goodsID(beverageGoods.getGoodsID())
							.goodsByPrice(beverageGoods.getGoodsPrice())
							.buyQuantity(buyQuantity).build();
					beverageOrderDao.save(creatOrder);
					
					//送回顯示畫面的:
					OrderGoodsList orderGoodsList=OrderGoodsList.builder()
							.goodsID(beverageGoods.getGoodsID())
							.goodsName(beverageGoods.getGoodsName())
							.description(beverageGoods.getDescription())
							.imageName(beverageGoods.getGoodsImageName())
							.price(beverageGoods.getGoodsPrice())
							.buyRealQuantity(buyQuantity)
							.build();
					orderGoodsAll.add(orderGoodsList);
//					cartGoods.clear();//如果完成訂單就清空購物車
//				}else {//就不動DB內庫存 & 也同時不建立訂單
//					logger.info("商品餘額不足,商品ID:" + beverageGoods.getGoodsID()+"商品名稱:"+beverageGoods.getGoodsName());
//				}
				
			}
		}
		
		checkoutCompleteInfo.setCustomer(customer);//customer只秀在頁面上 沒有進去DB內
		checkoutCompleteInfo.setOrderGoodsList(orderGoodsAll);
		return checkoutCompleteInfo;
	}
	
	public List<SellWellGoods> sellWellGoods(String startDate, String endDate) {
		List<SellWellGoods> sellWellGoods=beverageOrderDao.sellWellGoods(startDate,endDate);
		
		return sellWellGoods;
	}
}
