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

@CrossOrigin //跨站請求
@RestController
@RequestMapping("/ecommerce/BackendController")
public class BackendController {
		
	@Autowired
	private BackendService backendService;
		
	@ApiOperation(value = "購物網-後臺-商品新增作業")
	@PostMapping(value = "/createGoods", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<BeverageGoods> createGoods(@ModelAttribute GoodsVo goodsVo) throws IOException {
		/*
		 * 可以為空的有:goodsID,imageName,description 其餘必須要給值
		 */
		BeverageGoods goods = backendService.createGoods(goodsVo);
		return ResponseEntity.ok(goods);
	}
	
	@ApiOperation(value = "購物網-後臺-商品維護作業-更新商品資料")
	//原本應該是put但是swagger關係 為了不更動swagger所以先改為post
	@PostMapping(value = "/updateGoods", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<BeverageGoods> updateGoods(@ModelAttribute GoodsVo goodsVo) throws IOException {
		//必填入的數值為:goodID
		BeverageGoods goods = backendService.updateGoods(goodsVo);
		return ResponseEntity.ok(goods);
	}
	
	@ApiOperation(value = "購物網-後臺-商品維護作業-查詢全部商品清單")
	@GetMapping(value = "/queryAllGoods")//此設計為ID從大到小排序
	public ResponseEntity<List<BeverageGoods>> queryAllGoods() {
		
		List<BeverageGoods> goodsDatas = backendService.queryAllGoods();
		return ResponseEntity.ok(goodsDatas);
	}
	
	@ApiOperation(value = "購物網-後臺-商品維護作業-查詢單一商品資料")
	@GetMapping(value = "/queryGoodsByID")
	public ResponseEntity<BeverageGoods> queryGoodsByID(@RequestParam long goodsID){
		
		BeverageGoods goodsData = backendService.queryGoodsByID(goodsID);
		
		return ResponseEntity.ok(goodsData);
	}
	
	
	@ApiOperation(value = "購物網-後臺-商品訂單查詢(一個商品對應到多筆訂單)")
	@GetMapping(value = "/queryGoodsSales") //設計從訂單最新的開始排序(也就是orderID DESC)
	public ResponseEntity<GoodsReportSalesInfo> queryGoodsSales(
			 @RequestParam String startDate, @RequestParam String endDate,  
			 @RequestParam int currentPageNo, @RequestParam int pageDataSize, @RequestParam int pagesIconSize,
			 @RequestParam String orderByItem,@RequestParam String sort) {
		/*
		 startDate:2023-03-01 2023/05/31 兩種都可以
		 endDate:2023-03-31
		 currentPageNo:1
		 pageDataSize: 5
		 pagesIconSize: 3
		 orderByItem:goodsID,goodsBuyPrice,buyQuantity,customerName,goodsName
		 sort:ASC/DESC
		 */	
		GoodsSalesReportCondition condition = GoodsSalesReportCondition.builder().startDate(startDate).endDate(endDate)
											.sort(sort).orderByItem(orderByItem).build();
		
		GenericPageable genericPageable = GenericPageable.builder().currentPageNo(currentPageNo)
				.pageDataSize(pageDataSize).pagesIconSize(pagesIconSize).build();
		
		GoodsReportSalesInfo goodsReportSalesInfo = backendService.queryGoodsSales(condition, genericPageable);
		
		return ResponseEntity.ok(goodsReportSalesInfo);
	}
	
	@ApiOperation(value = "購物網-後臺-查詢商品列表")
	@GetMapping(value = "/queryGoodsData")
	public ResponseEntity<GoodsDataInfo> queryGoodsData(@RequestParam(required = false) Integer goodsID, 
			 @RequestParam(required = false) String goodsName, @RequestParam(required = false) String sort,
			 @RequestParam(required = false) String orderByItem,
			 @RequestParam(required = false) Integer startPrice, @RequestParam(required = false) Integer endPrice, 
			 @RequestParam(required = false) Integer quantity, @RequestParam(required = false) String status,
			 @RequestParam int currentPageNo, @RequestParam int pageDataSize, @RequestParam int pagesIconSize) {
		/*
		 * 一定要輸入的欄位有:currentPageNo,pageDataSize,pagesIconSize
		 * sort:ASC/DESC
		 * orderByItem(排序條件):goodsID,goodsName,goodsPrice,goodsQuantity,status
		 */
		GoodsDataCondition condition = GoodsDataCondition.builder().goodsID(goodsID).goodsName(goodsName)
				.startPrice(startPrice).endPrice(endPrice)
				.sort(sort).orderByItem(orderByItem).quantity(quantity).status(status).build();
		
		GenericPageable genericPageable = GenericPageable.builder().currentPageNo(currentPageNo)
				.pageDataSize(pageDataSize).pagesIconSize(pagesIconSize).build();
				
		GoodsDataInfo goodsDataInfo = backendService.queryGoodsData(condition, genericPageable);		
		
		return ResponseEntity.ok(goodsDataInfo);
	}
	
	@ApiOperation(value = "購物網-後臺-商品訂單報表分析")
	@GetMapping(value = "/queryGoodsSalesReport") //設計從訂單最新的開始排序(也就是orderID DESC)
	public ResponseEntity<List<GoodsReport>> queryGoodsSalesReport(@RequestParam String startDate, 
			@RequestParam String endDate){
		
		List<GoodsReport> goodsReports=backendService.queryGoodsSalesReport(startDate,endDate);
		return ResponseEntity.ok(goodsReports);
	}
	
	@ApiOperation(value = "購物網-後臺-列印全部商品列表")
	@GetMapping("/jasperPdfExport")
	 public void exportPDF(HttpServletResponse response) throws IOException, JRException {
	  response.setContentType("application/pdf");
	  DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
	  String currentDateTime = dateFormatter.format(new Date());
	  String headerKey = "Content-Disposition";
	  String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
	  response.setHeader(headerKey, headerValue);
	  backendService.exportPDF(response);
	 }
	
	
}
