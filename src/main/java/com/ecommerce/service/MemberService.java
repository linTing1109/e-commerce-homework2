package com.ecommerce.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.dao.BeverageGoodsDao;
import com.ecommerce.dao.BeverageMemberDao;
import com.ecommerce.dao.BeverageOrderDao;
import com.ecommerce.entity.BeverageGoods;
import com.ecommerce.entity.BeverageMember;
import com.ecommerce.vo.GenericPageable;
import com.ecommerce.vo.GoodsReportSales;
import com.ecommerce.vo.GoodsReportSalesInfo;
import com.ecommerce.vo.GoodsSalesReportCondition;
import com.ecommerce.vo.GoodsVo;
import com.ecommerce.vo.MemberInfo;
import com.ecommerce.vo.MemberInfoVo;

@Service
public class MemberService {
private static Logger logger = LoggerFactory.getLogger(MemberService.class);
	
	@Autowired
	private BeverageGoodsDao beverageGoodsDao;
	
	@Autowired
	private BeverageMemberDao beverageMemberDao;
	
	@Autowired
	private BeverageOrderDao beverageOrderDao;

	@Resource(name = "sessionCartGoods")
	private List<GoodsVo> cartGoods;
	
	@Resource(name="member")
	private MemberInfo sessionMemberInfo;
	
	public List<GoodsVo> addCartGoods(GoodsVo goodsVo){
		BeverageGoods beverageGoods = beverageGoodsDao.findByGoodsID(goodsVo.getGoodsID());
		//運用id查到實際商品資訊 塞回去秀出來
		if(beverageGoods !=null) { //有這筆ID才放入購物車 否則不放入
			goodsVo = GoodsVo.builder()
					.goodsID(beverageGoods.getGoodsID())
					.goodsName(beverageGoods.getGoodsName())
					.description(beverageGoods.getDescription())
					.price(beverageGoods.getGoodsPrice())
					.quantity(beverageGoods.getGoodsQuantity())
					.imageName(beverageGoods.getGoodsImageName()).build();
			cartGoods.add(goodsVo);
		}
		
		return cartGoods;
				
	}
	
	public MemberInfoVo addMember(MemberInfoVo memberInfoVo){
		BeverageMember beverageMember=beverageMemberDao.findByIdentificationNo(memberInfoVo.getIdentificationNo());
		if(beverageMember !=null) {//帳號存在
//			beverageMember = null;
			memberInfoVo.setLoginMessage("帳號存在 新增失敗");
			memberInfoVo.setIsLogin(false);
			logger.info("帳號存在 新增失敗");
		}else {
			beverageMember=new BeverageMember();
			beverageMember.setIdentificationNo(memberInfoVo.getIdentificationNo());
	        beverageMember.setCustomerName(memberInfoVo.getCusName());
	        beverageMember.setPassword(memberInfoVo.getCusPassword());
			beverageMemberDao.save(beverageMember);
			memberInfoVo.setIsLogin(true);
			memberInfoVo.setLoginMessage("帳號新增成功");
		}
		return memberInfoVo;
	}
	
	public GoodsReportSalesInfo queryGoodsSales(GoodsSalesReportCondition condition, GenericPageable genericPageable,String cusName) {		
		//算出資料開始的頭與尾
		int rowStart=((genericPageable.getCurrentPageNo()-1)*genericPageable.getPageDataSize())+1;
		int rowEnd=((genericPageable.getCurrentPageNo()-1)*genericPageable.getPageDataSize())+genericPageable.getPageDataSize();

		String sort=condition.getSort();//排序設定
		String orderByItem=condition.getOrderByItem();//排序類別
		GoodsReportSalesInfo goodsReportSalesInfo=new GoodsReportSalesInfo();
//		List<GoodsReportSales> goodsReportSales=beverageOrderDao.queryGoodSalesNumberASC(condition.getStartDate(),condition.getEndDate(),rowStart,rowEnd,orderByItem);	
		List<GoodsReportSales> goodsReportSales=new LinkedList<>();
		if(sort.equals("ASC") ) {// 如果是排序商品名稱  (也就是文字類型)
			if(orderByItem.equals("goodsName") ) {
				goodsReportSales=beverageOrderDao.queryMemberOrderTextASC(condition.getStartDate(),condition.getEndDate(),rowStart,rowEnd,orderByItem,cusName);
			}else{ //排序的是數字類型
				goodsReportSales=beverageOrderDao.queryMemberOrderNumberASC(condition.getStartDate(),condition.getEndDate(),rowStart,rowEnd,orderByItem,cusName);	
			}
		}else {
			if(orderByItem.equals("goodsName")) {
				goodsReportSales=beverageOrderDao.queryMemberOrderTextDESC(condition.getStartDate(),condition.getEndDate(),rowStart,rowEnd,orderByItem,cusName);
			}else{
				goodsReportSales=beverageOrderDao.queryMemberOrderNumberDESC(condition.getStartDate(),condition.getEndDate(),rowStart,rowEnd,orderByItem,cusName);	
			}
		}
			
		//將訂單資訊&頁面相關 塞回goodsReportSalesInfo
		goodsReportSalesInfo.setGoodsReportSalesList(goodsReportSales);
		genericPageable.setDataTotalSize(goodsReportSales.get(0).getOrderCount());//將總筆數統一放入genericPageable內
		//給容器讓Pagination可以塞值
		Set<Integer> number=new HashSet<>();
		genericPageable.setPagination(number);
		
		goodsReportSalesInfo.setGenericPageable(genericPageable);
		
		return goodsReportSalesInfo;
	}
}
