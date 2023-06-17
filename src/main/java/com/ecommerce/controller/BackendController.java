package com.ecommerce.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.entity.BeverageGoods;
import com.ecommerce.service.BackendService;
import com.ecommerce.vo.GenericPageable;
import com.ecommerce.vo.GoodsDataCondition;
import com.ecommerce.vo.GoodsDataInfo;
import com.ecommerce.vo.GoodsReport;
import com.ecommerce.vo.GoodsReportSalesInfo;
import com.ecommerce.vo.GoodsSalesReportCondition;
import com.ecommerce.vo.GoodsVo;

import io.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.JRException;

//跨站請求
//對應 React axios 設置 {withCredentials: true} HttpHeader就能帶有Cookie夾帶JESSIONID 
//Server後端必須設置 Access-Control-Allow-Origin(不能為*)、Access-Control-Allow-Credentials(必須為true)
@CrossOrigin 
@RestController //標註於Controller類別上 等於@Controller+@ResponseBody(JSON)
@RequestMapping("/ecommerce/BackendController")//決定所有API共同的前綴fprefixesURL
public class BackendController {
	
	//透過Autowired的方式注入不同的實作
	@Autowired
	private BackendService backendService;
		
	@ApiOperation(value = "購物網-後臺-商品新增作業") //@ApiOperation描述API功能說明
	//@PostMapping:對應http POST 資料新增  並且表示該請求的媒體類型為 multipart/form-data
	@PostMapping(value = "/createGoods", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<BeverageGoods> createGoods(@ModelAttribute GoodsVo goodsVo) throws IOException {
		/*
		 * 可以為空的有:goodsID,imageName,description 其餘必須要給值
		 */
		BeverageGoods goods = backendService.createGoods(goodsVo);
		return ResponseEntity.ok(goods);//將回傳的POJO物件轉為JSON格式
	}
	
	@ApiOperation(value = "購物網-後臺-商品維護作業-更新商品資料")//@ApiOperation描述API功能說明
	//原本應該是put但是swagger關係 為了不更動swagger所以先改為post
	@PostMapping(value = "/updateGoods", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<BeverageGoods> updateGoods(@ModelAttribute GoodsVo goodsVo) throws IOException {
		//必填入的數值為:goodID
		BeverageGoods goods = backendService.updateGoods(goodsVo);
		return ResponseEntity.ok(goods);//將回傳的POJO物件轉為JSON格式
	}
	
	@ApiOperation(value = "購物網-後臺-商品維護作業-查詢全部商品清單")//@ApiOperation描述API功能說明
	@GetMapping(value = "/queryAllGoods")//此設計為ID從大到小排序 @GetMapping:對應http GET 資料查詢
	public ResponseEntity<List<BeverageGoods>> queryAllGoods() {
		
		List<BeverageGoods> goodsDatas = backendService.queryAllGoods();
		return ResponseEntity.ok(goodsDatas);//將回傳的POJO物件轉為JSON格式
	}
	
	@ApiOperation(value = "購物網-後臺-商品維護作業-查詢單一商品資料")//@ApiOperation描述API功能說明
	@GetMapping(value = "/queryGoodsByID") //@GetMapping:對應http GET 資料查詢
	public ResponseEntity<BeverageGoods> queryGoodsByID(@RequestParam long goodsID){
		//@RequestParam:用於Controller method參數上 取得http請求parameter帶入方法參數
		BeverageGoods goodsData = backendService.queryGoodsByID(goodsID);
		
		return ResponseEntity.ok(goodsData);//將回傳的POJO物件轉為JSON格式
	}
	
	
	@ApiOperation(value = "購物網-後臺-商品訂單查詢(一個商品對應到多筆訂單)") //@ApiOperation描述API功能說明
	@GetMapping(value = "/queryGoodsSales") //設計從訂單最新的開始排序(也就是orderID DESC) @GetMapping:對應http GET 資料查詢
	public ResponseEntity<GoodsReportSalesInfo> queryGoodsSales(
			 @RequestParam String startDate, @RequestParam String endDate,  
			 @RequestParam int currentPageNo, @RequestParam int pageDataSize, @RequestParam int pagesIconSize,
			 @RequestParam String orderByItem,@RequestParam String sort) {
		/*
		 @RequestParam:用於Controller method參數上 取得http請求parameter帶入方法參數
		 startDate:2023-03-01 2023/05/31 兩種都可以
		 endDate:2023-03-31
		 currentPageNo:1
		 pageDataSize: 5
		 pagesIconSize: 3
		 orderByItem:goodsID,goodsBuyPrice,buyQuantity,customerName,goodsName
		 sort:ASC/DESC
		 */
		//使用建構者模式 將分別的資料塞入後 build根據屬性值建立物件實例
		GoodsSalesReportCondition condition = GoodsSalesReportCondition.builder().startDate(startDate).endDate(endDate)
											.sort(sort).orderByItem(orderByItem).build();
		
		GenericPageable genericPageable = GenericPageable.builder().currentPageNo(currentPageNo)
				.pageDataSize(pageDataSize).pagesIconSize(pagesIconSize).build();
		
		GoodsReportSalesInfo goodsReportSalesInfo = backendService.queryGoodsSales(condition, genericPageable);
		
		return ResponseEntity.ok(goodsReportSalesInfo);//將回傳的POJO物件轉為JSON格式
	}
	
	@ApiOperation(value = "購物網-後臺-查詢商品列表")//@ApiOperation描述API功能說明
	@GetMapping(value = "/queryGoodsData") //@GetMapping:對應http GET 資料查詢
	public ResponseEntity<GoodsDataInfo> queryGoodsData(@RequestParam(required = false) Integer goodsID, 
			 @RequestParam(required = false) String goodsName, @RequestParam(required = false) String sort,
			 @RequestParam(required = false) String orderByItem,
			 @RequestParam(required = false) Integer startPrice, @RequestParam(required = false) Integer endPrice, 
			 @RequestParam(required = false) Integer quantity, @RequestParam(required = false) String status,
			 @RequestParam int currentPageNo, @RequestParam int pageDataSize, @RequestParam int pagesIconSize) {
		/*
		 * @RequestParam:用於Controller method參數上 取得http請求parameter帶入方法參數
		 * 一定要輸入的欄位有:currentPageNo,pageDataSize,pagesIconSize (required = false為非必填)
		 * sort:ASC/DESC
		 * orderByItem(排序條件):goodsID,goodsName,goodsPrice,goodsQuantity,status
		 */
		//使用建構者模式 將分別的資料塞入後 build根據屬性值建立物件實例
		GoodsDataCondition condition = GoodsDataCondition.builder().goodsID(goodsID).goodsName(goodsName)
				.startPrice(startPrice).endPrice(endPrice)
				.sort(sort).orderByItem(orderByItem).quantity(quantity).status(status).build();
		
		GenericPageable genericPageable = GenericPageable.builder().currentPageNo(currentPageNo)
				.pageDataSize(pageDataSize).pagesIconSize(pagesIconSize).build();
				
		GoodsDataInfo goodsDataInfo = backendService.queryGoodsData(condition, genericPageable);		
		
		return ResponseEntity.ok(goodsDataInfo);//將回傳的POJO物件轉為JSON格式
	}
	
	@ApiOperation(value = "購物網-後臺-商品訂單報表分析")//@ApiOperation描述API功能說明
	@GetMapping(value = "/queryGoodsSalesReport") //設計從訂單最新的開始排序(也就是orderID DESC) @GetMapping:對應http GET 資料查詢
	public ResponseEntity<List<GoodsReport>> queryGoodsSalesReport(@RequestParam String startDate, 
			@RequestParam String endDate){
		
		List<GoodsReport> goodsReports=backendService.queryGoodsSalesReport(startDate,endDate);
		return ResponseEntity.ok(goodsReports);//將回傳的POJO物件轉為JSON格式
	}
	
	@ApiOperation(value = "購物網-後臺-列印全部商品列表") //@ApiOperation描述API功能說明
	@GetMapping("/jasperPdfExport") //@GetMapping:對應http GET 資料查詢
	//主要功能設定回應內容PDF,並且設置相關回應投 呼叫後可以將方法回應的內容寫入 以利於瀏覽器下載
	 public void exportPDF(HttpServletResponse response) throws IOException, JRException {
	  response.setContentType("application/pdf"); //方法用於設定的內容類型,回應內容為PDF檔案
	  DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");//日期格式化為指定的格式
	  String currentDateTime = dateFormatter.format(new Date());//格式化當前的日期
	  String headerKey = "Content-Disposition";//Content-Disposition:為Response Header告訴瀏覽器如何處理
	  //attachment:指定檔案下載方式為附件,檔名為pdf_當前日期.pdf檔
	  String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
	  response.setHeader(headerKey, headerValue);//回應告訴瀏覽器該如何處理:視為下載附件
	  backendService.exportPDF(response);
	 }
	
	
}
