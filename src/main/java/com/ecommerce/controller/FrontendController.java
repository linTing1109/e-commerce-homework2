package com.ecommerce.controller;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.service.FrontendService;
import com.ecommerce.vo.CheckoutCompleteInfo;
import com.ecommerce.vo.GenericPageable;
import com.ecommerce.vo.GoodsVo;
import com.ecommerce.vo.MemberInfo;
import com.ecommerce.vo.OrderCustomer;
import com.ecommerce.vo.ProductGoodsInfo;
import com.ecommerce.vo.SellWellGoods;

import io.swagger.annotations.ApiOperation;

//跨站請求
//對應 React axios 設置 {withCredentials: true} HttpHeader就能帶有Cookie夾帶JESSIONID 
//Server後端必須設置 Access-Control-Allow-Origin(不能為*)、Access-Control-Allow-Credentials(必須為true)
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:8090"}, allowCredentials = "true") 
@RestController
@RequestMapping("/ecommerce/FrontendController")
public class FrontendController {
	
	private static Logger logger = LoggerFactory.getLogger(FrontendController.class);
	
	@Autowired
	private HttpSession httpSession;
	
	@Resource(name="member")
	private MemberInfo sessionMemberInfo;
	
	@Resource(name = "sessionCartGoods")
	private List<GoodsVo> cartGoods;
	
	@Autowired
	private FrontendService frontendService;
	
	@ApiOperation(value = "購物網-前臺-查詢商品列表")
	@GetMapping(value = "/queryGoodsData")
	public ResponseEntity<ProductGoodsInfo> queryGoodsData(@RequestParam(required = false) String searchKeyword,
			 @RequestParam int currentPageNo, @RequestParam int pageDataSize, @RequestParam int pagesIconSize) {
	
		GenericPageable genericPageable = GenericPageable.builder().currentPageNo(currentPageNo)
				.pageDataSize(pageDataSize).pagesIconSize(pagesIconSize).build();
		
		ProductGoodsInfo goodsDataInfo = frontendService.queryGoodsData(searchKeyword, genericPageable);		
		
		return ResponseEntity.ok(goodsDataInfo);
	}
	
	@ApiOperation(value = "購物網-前臺-結帳購物車商品")
	@PostMapping(value = "/checkoutGoods")
	public ResponseEntity<CheckoutCompleteInfo> checkoutGoods(@RequestBody OrderCustomer customer) {
		
		logger.info("HttpSession checkoutGoods:" + httpSession.getId());
		logger.info("CheckoutGoods:" + sessionMemberInfo.toString());
		
		CheckoutCompleteInfo checkoutCompleteInfo = frontendService.checkoutGoods(customer);

//		cartGoods.clear();//如果完成訂單就清空購物車	
		return ResponseEntity.ok(checkoutCompleteInfo);
	}
	
	@ApiOperation(value = "購物網-前臺-熱銷商品排行")
	@GetMapping(value = "/sellWellGoods") //設計從訂單最新的開始排序(也就是orderID DESC)
	public ResponseEntity<List<SellWellGoods>> sellWellGoods(@RequestParam String startDate, 
			@RequestParam String endDate){
		
		List<SellWellGoods> sellWellGoods=frontendService.sellWellGoods(startDate,endDate);
		return ResponseEntity.ok(sellWellGoods);
	}

}
