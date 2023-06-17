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
@RestController //標註於Controller類別上 等於@Controller+@ResponseBody(JSON)
@RequestMapping("/ecommerce/MemberController") //決定所有API共同的前綴fprefixesURL
public class MemberController {
	
	private static Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	//透過名稱:name屬性指定要注入的資源名稱 對應到MemberConfig檔
	@Resource(name="member")
	private MemberInfo sessionMemberInfo;
	
	@Resource(name = "sessionCartGoods") //要使用不同名字就需要在這邊寫上config那邊設定的名字
	private List<GoodsVo> cartGoods;
	
	//透過Autowired的方式注入不同的實作
	@Autowired
	private HttpSession httpSession; 
	
	@Autowired
	private MemberService memberService; 
	
	@Autowired
	private BeverageMemberDao beverageMemberDao;

	@ApiOperation(value = "購物網-會員-檢查登入") //@ApiOperation描述API功能說明
	@GetMapping(value = "/checkLogin") //@GetMapping:對應http GET 資料查詢
	public ResponseEntity<MemberInfo> checkLogin() {
		
		logger.info("HttpSession checkLogin:" + httpSession.getId());
		logger.info("CheckLogin:" + sessionMemberInfo.toString());
		return ResponseEntity.ok(sessionMemberInfo);//ResponseEntity<T>將回傳的POJO物件轉為JSON格式
	}
	
	@ApiOperation(value = "購物網-會員-登入") //@ApiOperation描述API功能說明
	@PostMapping(value = "/login") //@PostMapping:對應http POST 資料新增
	public ResponseEntity<MemberInfo> login(@RequestBody MemberInfoVo memberInfoVo) {
		//@RequestBody:標註於Controller method方法參數上 支援參數傳入JSON格式 會自動轉換參數為POJO物件
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
		
		return ResponseEntity.ok(sessionMemberInfo);//ResponseEntity<T>將回傳的POJO物件轉為JSON格式
	}
	
	@ApiOperation(value = "購物網-會員-新增會員")//@ApiOperation描述API功能說明
	@PostMapping(value = "/addMemberLogin") //@PostMapping:對應http POST 資料新增
	public ResponseEntity<MemberInfoVo> addMemberLogin(@RequestBody MemberInfoVo memberInfoVo) {
		//@RequestBody:標註於Controller method方法參數上 支援參數傳入JSON格式 會自動轉換參數為POJO物件
		MemberInfoVo memberInfoVoResult=memberService.addMember(memberInfoVo);

		return ResponseEntity.ok(memberInfoVoResult);//ResponseEntity<T>將回傳的POJO物件轉為JSON格式
	}
	
	@ApiOperation(value = "購物網-會員-登出") //@ApiOperation描述API功能說明
	@GetMapping(value = "/logout") //@GetMapping:對應http GET 資料查詢
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
	
	@ApiOperation(value = "商品加入購物車")//@ApiOperation描述API功能說明
	@PostMapping(value = "/addCartGoods")//@PostMapping:對應http POST 資料新增
	public ResponseEntity<List<GoodsVo>> addCartGoods(@RequestBody GoodsVo goodsVo) {
		/*
		 *  @RequestBody:標註於Controller method方法參數上 支援參數傳入JSON格式 會自動轉換參數為POJO物件
		 	設定直接丟id即可
			{
			  "goodsID": 100
			}

		 */
		List<GoodsVo> carGoodsVos=memberService.addCartGoods(goodsVo);

		return ResponseEntity.ok(carGoodsVos);//ResponseEntity<T>將回傳的POJO物件轉為JSON格式
	}
	
	@ApiOperation(value = "查詢購物車商品")//@ApiOperation描述API功能說明
	@GetMapping(value = "/queryCartGoods")//@GetMapping:對應http GET 資料查詢
	public ResponseEntity<List<GoodsVo>> queryCartGoods() {

		return ResponseEntity.ok(cartGoods);//ResponseEntity<T>將回傳的POJO物件轉為JSON格式
	}
	
	@ApiOperation(value = "刪除單一(全部)商品購物車")//@ApiOperation描述API功能說明
	@PostMapping(value = "/delOneGoodsCart")//@PostMapping:對應http POST
	public ResponseEntity<List<GoodsVo>> delOneGoodsCart(@RequestBody GoodsVo goodsVoAll) {
		//@RequestBody:標註於Controller method方法參數上 支援參數傳入JSON格式 會自動轉換參數為POJO物件
		List<GoodsVo> remainingGoods = new ArrayList<>();//這是要存更新後的資料
		// 遍歷商品列表，檢查每個商品的goodsID
	    for (GoodsVo goods : cartGoods) {
	        if (goods.getGoodsID() != goodsVoAll.getGoodsID()) {
	            remainingGoods.add(goods); // 將不匹配的商品添加到剩餘商品列表
	        }
	    }
	    //在將過濾好的資料塞回去購物車內
	    cartGoods.clear(); // 清空cartGoods列表
	    cartGoods.addAll(remainingGoods); // 將更新後的資料添加回cartGoods列表
		return ResponseEntity.ok(cartGoods);//ResponseEntity<T>將回傳的POJO物件轉為JSON格式
	}
	
	@ApiOperation(value = "減少單一商品購物車")//@ApiOperation描述API功能說明
	@PostMapping(value = "/reduceOneGoodsCart")//@PostMapping:對應http POST
	public ResponseEntity<List<GoodsVo>> reduceOneGoodsCart(@RequestBody GoodsVo goodsVo2) {
		//@RequestBody:標註於Controller method方法參數上 支援參數傳入JSON格式 會自動轉換參數為POJO物件
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
		return ResponseEntity.ok(cartGoods);//ResponseEntity<T>將回傳的POJO物件轉為JSON格式
	}
	
	
	@ApiOperation(value = "清空購物車商品")//@ApiOperation描述API功能說明
	@DeleteMapping(value = "/clearCartGoods") //@DeleteMapping 對應http delete用於資料刪除
	public ResponseEntity<List<GoodsVo>> clearCartGoods() {
		cartGoods.clear();
		return ResponseEntity.ok(cartGoods);//將回傳的POJO物件轉為JSON格式
	}
	
	@ApiOperation(value = "購物網-前臺-會員商品訂單查詢")
	@GetMapping(value = "/queryGoodsSales") //設計從訂單最新的開始排序(也就是orderID DESC)
	public ResponseEntity<GoodsReportSalesInfo> queryGoodsSales(
			//@RequestParam:用於Controller method參數上 取得http請求parameter帶入方法參數
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
		//使用建構者模式 將分別的資料塞入後 build根據屬性值建立物件實例
		GoodsSalesReportCondition condition = GoodsSalesReportCondition.builder().startDate(startDate).endDate(endDate)
											.sort(sort).orderByItem(orderByItem).build();
		
		GenericPageable genericPageable = GenericPageable.builder().currentPageNo(currentPageNo)
				.pageDataSize(pageDataSize).pagesIconSize(pagesIconSize).build();
		
		GoodsReportSalesInfo goodsReportSalesInfo = memberService.queryGoodsSales(condition, genericPageable,cusName);
		
		return ResponseEntity.ok(goodsReportSalesInfo);//將回傳的POJO物件轉為JSON格式
	}
}
