package com.ecommerce.controller;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dao.BeverageMemberDao;
import com.ecommerce.entity.BeverageMember;
import com.ecommerce.service.MemberService;
import com.ecommerce.vo.GenericPageable;
import com.ecommerce.vo.GoodsReportSalesInfo;
import com.ecommerce.vo.GoodsSalesReportCondition;
import com.ecommerce.vo.GoodsVo;
import com.ecommerce.vo.MemberInfo;
import com.ecommerce.vo.MemberInfoVo;

import io.swagger.annotations.ApiOperation;
//跨站請求
//對應 React axios 設置 {withCredentials: true} HttpHeader就能帶有Cookie夾帶JESSIONID 
//Server後端必須設置 Access-Control-Allow-Origin(不能為*)、Access-Control-Allow-Credentials(必須為true)
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:8090"}, allowCredentials = "true") 
@RestController
@RequestMapping("/ecommerce/MemberController")
public class MemberController {
	
	private static Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	@Resource(name="member")
	private MemberInfo sessionMemberInfo;
	
	@Resource(name = "sessionCartGoods") //要使用不同名字就需要在這邊寫上config那邊設定的名字
	private List<GoodsVo> cartGoods;
	
	@Autowired
	private HttpSession httpSession; 
	
	@Autowired
	private MemberService memberService; 
	
	@Autowired
	private BeverageMemberDao beverageMemberDao;

	@ApiOperation(value = "購物網-會員-檢查登入")
	@GetMapping(value = "/checkLogin")
	public ResponseEntity<MemberInfo> checkLogin() {
		
		logger.info("HttpSession checkLogin:" + httpSession.getId());
		logger.info("CheckLogin:" + sessionMemberInfo.toString());
		return ResponseEntity.ok(sessionMemberInfo);
	}
	
	@ApiOperation(value = "購物網-會員-登入")
	@PostMapping(value = "/login")
	public ResponseEntity<MemberInfo> login(@RequestBody MemberInfoVo memberInfoVo) {
		/*
			{
			  "identificationNo": "A124243295",
			  "cusPassword": "123"
			}
			{
			  "identificationNo": "G436565447",
			  "cusPassword": "123"
			}
		 */
		logger.info("HttpSession Login:" + httpSession.getId());
		logger.info("Before:" + sessionMemberInfo.toString());
		BeverageMember beverageMember =beverageMemberDao.findByIdentificationNoAndPassword(memberInfoVo.getIdentificationNo(),memberInfoVo.getCusPassword());
		if(beverageMember!=null) {
//			if(beverageMember.getPassword().equals(memberInfoVo.getCusPassword())) {
				//因為不能把密碼秀在畫面上 所以就沒有塞值
				sessionMemberInfo.setIdentificationNo(beverageMember.getIdentificationNo());
				sessionMemberInfo.setCusName(beverageMember.getCustomerName());
				sessionMemberInfo.setLoginMessage(beverageMember.getCustomerName()+"，登入成功");
				sessionMemberInfo.setIsLogin(true);
//			}else {
//				sessionMemberInfo.setLoginMessage("輸入密碼錯誤");
//			}
			
		}else {
			sessionMemberInfo.setLoginMessage("帳密錯誤 請重新輸入");
		}
		
		logger.info("After:" + sessionMemberInfo.toString());
		
		return ResponseEntity.ok(sessionMemberInfo);
	}
	
	@ApiOperation(value = "購物網-會員-新增會員")
	@PostMapping(value = "/addMemberLogin")
	public ResponseEntity<MemberInfoVo> addMemberLogin(@RequestBody MemberInfoVo memberInfoVo) {
		
		MemberInfoVo memberInfoVoResult=memberService.addMember(memberInfoVo);

		return ResponseEntity.ok(memberInfoVoResult);
	}
	
	@ApiOperation(value = "購物網-會員-登出") 
	@GetMapping(value = "/logout")
	public ResponseEntity<MemberInfo> logout() {
		logger.info("HttpSession logout:" + httpSession.getId());
		
		//Attributes需要清除时，使用SessionStatus.setComplete();来清除
		//它只清除@SessionAttributes的session，不会清除HttpSession的数据
		//文獻參考:https://blog.csdn.net/hayre/article/details/54666275
//		httpSession.removeAttribute("sessionMemberInfo");
//		sessionStatus.setComplete();////標記session屬性已經完成
		
		//清空所有資訊
		sessionMemberInfo.setIsLogin(false);
		sessionMemberInfo.setIdentificationNo(null);
		sessionMemberInfo.setCusName(null);
		sessionMemberInfo.setIdentificationNo(null);
		return ResponseEntity.ok(MemberInfo.builder().build());
	}
	
	@ApiOperation(value = "商品加入購物車")
	@PostMapping(value = "/addCartGoods")
	public ResponseEntity<List<GoodsVo>> addCartGoods(@RequestBody GoodsVo goodsVo) {
		/*
		 	設定直接丟id即可
			{
			  "goodsID": 100
			}
			
			{
			  "goodsID": 28,
			  "goodsName": "Java Chip",
			  "description": "暢銷口味之一，以摩卡醬、乳品及可可碎片調製，加上細緻鮮奶油及摩卡醬，濃厚的巧克力風味。",
			  "imageName": "20130813154445805.jpg",
			  "price": 145,
			  "quantity": 17
			}

		 */
		List<GoodsVo> carGoodsVos=memberService.addCartGoods(goodsVo);

		return ResponseEntity.ok(carGoodsVos);
	}
	
	@ApiOperation(value = "查詢購物車商品")
	@GetMapping(value = "/queryCartGoods")
	public ResponseEntity<List<GoodsVo>> queryCartGoods() {

		return ResponseEntity.ok(cartGoods);
	}
	
	@ApiOperation(value = "刪除單一(全部)商品購物車")
	@PostMapping(value = "/delOneGoodsCart")
	public ResponseEntity<List<GoodsVo>> delOneGoodsCart(@RequestBody GoodsVo goodsVoAll) {
		List<GoodsVo> remainingGoods = new ArrayList<>();//這是要存更新後的資料
//		List<GoodsVo> changeGoods=cartGoods;
		// 遍歷商品列表，檢查每個商品的goodsID
	    for (GoodsVo goods : cartGoods) {
	        if (goods.getGoodsID() != goodsVoAll.getGoodsID()) {
	            remainingGoods.add(goods); // 將不匹配的商品添加到剩餘商品列表
	        }
	    }
	    //在將過濾好的資料塞回去購物車內
	    cartGoods.clear(); // 清空cartGoods列表
	    cartGoods.addAll(remainingGoods); // 將更新後的資料添加回cartGoods列表
		return ResponseEntity.ok(cartGoods);
	}
	
	@ApiOperation(value = "減少單一商品購物車")
	@PostMapping(value = "/reduceOneGoodsCart")
	public ResponseEntity<List<GoodsVo>> reduceOneGoodsCart(@RequestBody GoodsVo goodsVo2) {
		List<GoodsVo> remainingGoods = new ArrayList<>();//這是要存更新後的資料
		boolean deleted = false; // 判斷是否已執行刪除操作的標誌 執行過就不在執行
		
		// 遍歷商品列表，檢查每個商品的goodsID
	    for (GoodsVo goods : cartGoods) {
	        if (goods.getGoodsID() == goodsVo2.getGoodsID() && !deleted) {
	        	deleted=true;//這樣之後相同的進來也不會不見
	        }else {
	        	remainingGoods.add(goods); // 將不匹配的商品添加到剩餘商品列表
	        }
	    }
	    //在將過濾好的資料塞回去購物車內
	    cartGoods.clear(); // 清空cartGoods列表
	    cartGoods.addAll(remainingGoods); // 將更新後的資料添加回cartGoods列表
		return ResponseEntity.ok(cartGoods);
	}
	
	
	@ApiOperation(value = "清空購物車商品")
	@DeleteMapping(value = "/clearCartGoods")
	public ResponseEntity<List<GoodsVo>> clearCartGoods() {
//		httpSession.removeAttribute("sessionCartGoods");
//		sessionStatus.setComplete();
		cartGoods.clear();
		return ResponseEntity.ok(cartGoods);
	}
	
	@ApiOperation(value = "購物網-前臺-會員商品訂單查詢")
	@GetMapping(value = "/queryGoodsSales") //設計從訂單最新的開始排序(也就是orderID DESC)
	public ResponseEntity<GoodsReportSalesInfo> queryGoodsSales(
			 @RequestParam String startDate, @RequestParam String endDate,  
			 @RequestParam int currentPageNo, @RequestParam int pageDataSize, @RequestParam int pagesIconSize,
			 @RequestParam String orderByItem,@RequestParam String sort,@RequestParam String cusName) {
		/*
		 startDate:2023-03-01 2023/05/31 兩種都可以
		 endDate:2023-03-31
		 currentPageNo:1
		 pageDataSize: 5
		 pagesIconSize: 3
		 orderByItem:goodsID,goodsBuyPrice,buyQuantity,goodsName
		 sort:ASC/DESC
		 */	
		GoodsSalesReportCondition condition = GoodsSalesReportCondition.builder().startDate(startDate).endDate(endDate)
											.sort(sort).orderByItem(orderByItem).build();
		
		GenericPageable genericPageable = GenericPageable.builder().currentPageNo(currentPageNo)
				.pageDataSize(pageDataSize).pagesIconSize(pagesIconSize).build();
		
		GoodsReportSalesInfo goodsReportSalesInfo = memberService.queryGoodsSales(condition, genericPageable,cusName);
		
		return ResponseEntity.ok(goodsReportSalesInfo);
	}
}
