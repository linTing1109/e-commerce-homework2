package com.ecommerce.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.dao.BeverageGoodsDao;
import com.ecommerce.dao.BeverageOrderDao;
import com.ecommerce.dao.CriteriaQueryDao;
import com.ecommerce.entity.BeverageGoods;
import com.ecommerce.vo.GenericPageable;
import com.ecommerce.vo.GoodsDataCondition;
import com.ecommerce.vo.GoodsDataInfo;
import com.ecommerce.vo.GoodsReport;
import com.ecommerce.vo.GoodsReportSales;
import com.ecommerce.vo.GoodsReportSalesInfo;
import com.ecommerce.vo.GoodsSalesReportCondition;
import com.ecommerce.vo.GoodsVo;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class BackendService {

	private static Logger logger = LoggerFactory.getLogger(BackendService.class);
	
	@Autowired
	private BeverageGoodsDao beverageGoodsDao;
	
	@Autowired
	private BeverageOrderDao beverageOrderDao;
	
	@Autowired
	private CriteriaQueryDao criteriaQueryDao;

	public BeverageGoods createGoods(GoodsVo goodsVo) throws IOException {
		String fileName=null;
		if(goodsVo.getFile()!=null) {	
			// 複制檔案
			MultipartFile file = goodsVo.getFile();
			// 前端傳入檔案名稱
			if(goodsVo.getImageName()!=null) {
				fileName = goodsVo.getImageName();
			}else {
				fileName= file.getOriginalFilename();
			}
			// 複製檔案 並且檔案已經存在為覆蓋
			Files.copy(file.getInputStream(), Paths.get("/home/VendingMachine/DrinksImage").resolve(fileName),StandardCopyOption.REPLACE_EXISTING);
		}
		//從vo來的物件 放到@Entity物件內 (轉一手)
		BeverageGoods beverageGoods=BeverageGoods.builder()
				.goodsID(goodsVo.getGoodsID())
				.goodsName(goodsVo.getGoodsName())
				.description(goodsVo.getDescription())
				.goodsPrice(goodsVo.getPrice())
				.goodsQuantity(goodsVo.getQuantity())
				.goodsImageName(fileName)
				.status(goodsVo.getStatus()).build();
		
		return beverageGoodsDao.save(beverageGoods);
	}
	@Transactional
	public BeverageGoods updateGoods(GoodsVo goodsVo) throws IOException {
		String fileName=null;
		if(goodsVo.getFile()!=null) {	
			// 複制檔案
			MultipartFile file = goodsVo.getFile();
			// 前端傳入檔案名稱
			if(goodsVo.getImageName()!=null) {
				fileName = goodsVo.getImageName();
			}else {
				fileName= file.getOriginalFilename();
			}
			// 複製檔案 並且檔案已經存在為覆蓋
			Files.copy(file.getInputStream(), Paths.get("/home/VendingMachine/DrinksImage").resolve(fileName),StandardCopyOption.REPLACE_EXISTING);
		}
		// 搭配使用 @Transactional 查詢載入的物件受到 PersistenceContext 的管理
		Optional<BeverageGoods> optbeverageGoods = beverageGoodsDao.findById(goodsVo.getGoodsID());
		BeverageGoods beverageGoods = null;
		if(optbeverageGoods.isPresent()) {
			beverageGoods = optbeverageGoods.get();
			if(goodsVo.getGoodsName()!=null) beverageGoods.setGoodsName(goodsVo.getGoodsName());
			if(goodsVo.getDescription()!=null) beverageGoods.setDescription(goodsVo.getDescription());
			if(goodsVo.getPrice()!=0) beverageGoods.setGoodsPrice(goodsVo.getPrice());
			if(goodsVo.getQuantity()!=0) beverageGoods.setGoodsQuantity(goodsVo.getQuantity()+beverageGoods.getGoodsQuantity());
			if(goodsVo.getFile()!=null) beverageGoods.setGoodsImageName(fileName);
			if(goodsVo.getStatus()!=null) beverageGoods.setStatus(goodsVo.getStatus());
		}
		
		return beverageGoodsDao.save(beverageGoods);
	}
	public List<BeverageGoods> queryAllGoods(){
		List<BeverageGoods> beverageGoods = beverageGoodsDao.findByGoodsIDIsNotNullOrderByGoodsIDDesc();
		
		return beverageGoods;
	}
	 
	public BeverageGoods queryGoodsByID(long goodsID){
		BeverageGoods beverageGoods=new BeverageGoods();
		Optional<BeverageGoods> beverageGoodsOpt = beverageGoodsDao.findById(goodsID);
		if(beverageGoodsOpt.isPresent()) {
			beverageGoods = beverageGoodsOpt.get();
		}else {
			logger.info("查詢結果無此帳號");
		}
		
		return beverageGoods;
	}
	
	public GoodsReportSalesInfo queryGoodsSales(GoodsSalesReportCondition condition, GenericPageable genericPageable) {		
		//算出資料開始的頭與尾
		int rowStart=((genericPageable.getCurrentPageNo()-1)*genericPageable.getPageDataSize())+1;
		int rowEnd=((genericPageable.getCurrentPageNo()-1)*genericPageable.getPageDataSize())+genericPageable.getPageDataSize();

//		String sort="ASC";//排序設定
//		String orderByItem="goodsID";//排序類別
		String sort=condition.getSort();//排序設定
		String orderByItem=condition.getOrderByItem();//排序類別
		GoodsReportSalesInfo goodsReportSalesInfo=new GoodsReportSalesInfo();
//		List<GoodsReportSales> goodsReportSales=beverageOrderDao.queryGoodSalesNumberASC(condition.getStartDate(),condition.getEndDate(),rowStart,rowEnd,orderByItem);	
		List<GoodsReportSales> goodsReportSales=new LinkedList<>();
		if(sort.equals("ASC") ) {// 如果是排序商品名稱跟客戶名稱  (也就是文字類型)
			if(orderByItem.equals("customerName") || orderByItem.equals("goodsName") ) {
				goodsReportSales=beverageOrderDao.queryGoodSalesTextASC(condition.getStartDate(),condition.getEndDate(),rowStart,rowEnd,orderByItem);
			}else{ //排序的是數字類型
				goodsReportSales=beverageOrderDao.queryGoodSalesNumberASC(condition.getStartDate(),condition.getEndDate(),rowStart,rowEnd,orderByItem);	
			}
		}else {
			if(orderByItem.equals("customerName") || orderByItem.equals("goodsName")) {
				goodsReportSales=beverageOrderDao.queryGoodSalesTextDESC(condition.getStartDate(),condition.getEndDate(),rowStart,rowEnd,orderByItem);
			}else{
				goodsReportSales=beverageOrderDao.queryGoodSalesNumberDESC(condition.getStartDate(),condition.getEndDate(),rowStart,rowEnd,orderByItem);	
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
//	//目前正確的版本 
//	public GoodsReportSalesInfo queryGoodsSales(GoodsSalesReportCondition condition, GenericPageable genericPageable) {
//		//String先轉換成data類型 再轉成自己想要的日期格式
//		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//		Date dateStart=null;
//		Date dateEnd=null;
//		//當兩個輸入來源不等於空的時候才轉換
//		if(condition.getStartDate() != null && condition.getEndDate() != null ) {
//			try {
//				dateStart=inputFormat.parse(condition.getStartDate());
//				dateEnd=inputFormat.parse(condition.getEndDate());
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//		} else {
//			logger.info("日期輸入有空值 請重新確認");
//		}
//		//將date轉換為localDateTime
//		LocalDateTime localDateStart=dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//		LocalDateTime localDateEnd=dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//		
//		GoodsReportSalesInfo goodsReportSalesInfo=new GoodsReportSalesInfo();
//		List<BeverageOrder> beverageOrders = beverageOrderDao.findByOrderDateBetween(localDateStart,localDateEnd);
//		//先測試單純無分頁的搜尋是否可以成功  先塞還沒有分頁之前的
////		goodsReportSalesInfo.setGoodsReportSalesList(beverageOrders);
//		genericPageable.setDataTotalSize(beverageOrders.size());//符合搜尋日期的總筆數(不含分頁)
//		
//		//page頁數從0開始   PageRequest.of(int page, int size, Sort sort)
//		Pageable pageable = PageRequest.of(genericPageable.getCurrentPageNo()-1, genericPageable.getPageDataSize(), 
//				Sort.by("orderID").descending()
//			);
//		//含分頁的搜尋
//		List<BeverageOrder> ordersPage = beverageOrderDao.findByOrderDateBetweenAndOrderIDIsNotNull(localDateStart,localDateEnd,pageable);
//		//給容器讓Pagination可以塞值
//		Set<Integer> number=new HashSet<>();
//		genericPageable.setPagination(number);
//		
//		
//		//將訂單資訊&頁面相關 塞回goodsReportSalesInfo
//		goodsReportSalesInfo.setGoodsReportSalesList(ordersPage);
//		goodsReportSalesInfo.setGenericPageable(genericPageable);
//		
//		return goodsReportSalesInfo;
//	}
	
	
	public GoodsDataInfo queryGoodsData(GoodsDataCondition condition, GenericPageable genericPageable) {
		GoodsDataInfo goodsDataInfo=new GoodsDataInfo();
		List<BeverageGoods> goodSearch = criteriaQueryDao.queryGoodsConditions(condition,genericPageable);
		
		//給容器讓Pagination可以塞值
		Set<Integer> number=new HashSet<>();
		genericPageable.setPagination(number);
		
		goodsDataInfo.setGoodsDatas(goodSearch);
		goodsDataInfo.setGenericPageable(genericPageable);
		return goodsDataInfo;
	}
	
	public List<GoodsReport> queryGoodsSalesReport(String startDate, String endDate) {
		List<GoodsReport> goodsReports=beverageOrderDao.queryGoodsSalesReport(startDate,endDate);
		
		return goodsReports;
	}
	
	public void exportPDF(HttpServletResponse response) throws JRException, IOException {
		  List<BeverageGoods> goodsList = queryAllGoods();
		  // Get file and compile it
		  File file = ResourceUtils.getFile("classpath:Coffee.jrxml");
		  JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
		  JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(goodsList);
		  Map<String, Object> parameters = new HashMap<>();
		  // Fill Jasper report
		  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
		  // Export report
//		        JasperExportManager.exportReportToPdfFile(jasperPrint,"/Users/mac_home/Downloads/goodsList.pdf");
		  JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		 }
	
	
}
