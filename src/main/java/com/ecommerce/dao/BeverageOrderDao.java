package com.ecommerce.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.entity.BeverageOrder;
import com.ecommerce.vo.GoodsReport;
import com.ecommerce.vo.GoodsReportSales;
import com.ecommerce.vo.SellWellGoods;

@Repository
public interface BeverageOrderDao extends JpaRepository<BeverageOrder, Long>{
	
	//後臺銷售報表使用
	/*SQL測試
	 * Select o.GOODS_ID as goodsID ,SUM(o.BUY_QUANTITY)as total, g.GOODS_NAME as goodsName
		from BEVERAGE_ORDER o,BEVERAGE_GOODS g
		where o.GOODS_ID=g.GOODS_ID
		and o.ORDER_DATE between '2023/05/25' and '2023/05/27'
		GROUP by o.GOODS_ID,g.GOODS_NAME
		order by total DESC;
	 * */
	@Query(value = 
			"Select o.GOODS_ID as goodsID ,SUM(o.BUY_QUANTITY)as total, g.GOODS_NAME as goodsName  " + 
			"from BEVERAGE_ORDER o,BEVERAGE_GOODS g " + 
			"where o.GOODS_ID=g.GOODS_ID\r\n" + 
			"and o.ORDER_DATE between TO_DATE(?1|| ' 00:00:00' ,'yyyy/MM/dd HH24:mi:ss')  and TO_DATE(?2 || ' 23:59:59', 'yyyy/MM/dd HH24:mi:ss' )  " + 
			"GROUP by o.GOODS_ID,g.GOODS_NAME " + 
			"order by total DESC ",
		nativeQuery = true)
	List<GoodsReport> queryGoodsSalesReport(String startDate, String endDate);
	
	//前臺熱銷表使用
	/*SQL測試:
	 * Select o.GOODS_ID as goodsID ,SUM(o.BUY_QUANTITY)as total, 
		g.GOODS_NAME as goodsName,g.DESCRIPTION as description,
		g.PRICE as price,g.QUANTITY as quantity,
		g.IMAGE_NAME as imageName,g.STATUS as status
		from BEVERAGE_ORDER o,BEVERAGE_GOODS g
		where o.GOODS_ID=g.GOODS_ID 
		and o.ORDER_DATE between '2023/04/27 00:00:00' and '2023/05/27 23:59:59'
		and g.STATUS='1' GROUP by o.GOODS_ID,g.GOODS_NAME,g.DESCRIPTION,g.PRICE,g.QUANTITY,g.IMAGE_NAME,g.STATUS
		order by total DESC;
			 * */
	@Query(value = 
			"Select o.GOODS_ID as goodsID ,SUM(o.BUY_QUANTITY)as total, " + 
			"g.GOODS_NAME as goodsName,g.DESCRIPTION as description, " + 
			"g.PRICE as price,g.QUANTITY as quantity, " + 
			"g.IMAGE_NAME as imageName,g.STATUS as status " + 
			"from BEVERAGE_ORDER o,BEVERAGE_GOODS g " + 
			"where o.GOODS_ID=g.GOODS_ID " + 
			"and o.ORDER_DATE between TO_DATE (?1|| ' 00:00:00' ,'yyyy/MM/dd HH24:mi:ss')  and TO_DATE(?2 || ' 23:59:59', 'yyyy/MM/dd HH24:mi:ss' ) " + 
			"and g.STATUS='1' GROUP by o.GOODS_ID,g.GOODS_NAME,g.DESCRIPTION,g.PRICE,g.QUANTITY,g.IMAGE_NAME,g.STATUS " + 
			"order by total DESC ",
		nativeQuery = true)
	List<SellWellGoods> sellWellGoods(String startDate, String endDate);
	
	
//	List<BeverageOrder> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
//	List<BeverageOrder> findByOrderDateBetweenAndOrderIDIsNotNull(LocalDateTime startDate, LocalDateTime endDate,Pageable pageable);
	/*這是SQL測試用
		 * SELECT roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount,quantity 
	FROM (
	    SELECT ROWNUM AS roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount,quantity 
	    FROM (
	        SELECT O.ORDER_ID AS orderID, O.ORDER_DATE AS orderDate, M.CUSTOMER_NAME AS customerName, G.GOODS_ID AS goodsID, G.GOODS_NAME AS goodsName,
	               O.GOODS_BUY_PRICE AS goodsBuyPrice, O.BUY_QUANTITY AS buyQuantity, COUNT(O.ORDER_ID) OVER () AS orderCount ,G.QUANTITY as quantity 
	        FROM BEVERAGE_ORDER O
	        JOIN BEVERAGE_MEMBER M ON O.CUSTOMER_ID = M.IDENTIFICATION_NO
	        JOIN BEVERAGE_GOODS G ON O.GOODS_ID = G.GOODS_ID
	        WHERE TRUNC(ORDER_DATE) BETWEEN TO_DATE('2023-01-01') AND TO_DATE('2023-05-31')
	        ORDER BY orderID ASC
	    )
	)
	WHERE roworderNumber BETWEEN 1 AND 33;
	 */
	String sqlOrderNumberASC =
			"SELECT roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount,quantity  " + 
			"FROM " + 
			"(SELECT ROWNUM AS roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount,quantity  " + 
			"FROM " + 
			"(SELECT O.ORDER_ID AS orderID, O.ORDER_DATE AS orderDate, M.CUSTOMER_NAME AS customerName, G.GOODS_ID AS goodsID, G.GOODS_NAME AS goodsName, " + 
			"O.GOODS_BUY_PRICE AS goodsBuyPrice, O.BUY_QUANTITY AS buyQuantity, COUNT(O.ORDER_ID) OVER () AS orderCount ,G.QUANTITY as quantity " + 
			"FROM BEVERAGE_ORDER O " + 
			"JOIN BEVERAGE_MEMBER M ON O.CUSTOMER_ID = M.IDENTIFICATION_NO " + 
			"JOIN BEVERAGE_GOODS G ON O.GOODS_ID = G.GOODS_ID " + 
			"WHERE TRUNC(ORDER_DATE) BETWEEN TO_DATE(?1, 'yyyy/MM/dd') AND TO_DATE(?2, 'yyyy/MM/dd') " + 
			"ORDER BY CASE ?5 "+
			"WHEN 'orderID' THEN orderID " +
			"WHEN 'goodsID' THEN goodsID " +
			"WHEN 'goodsBuyPrice' THEN goodsBuyPrice " +
			"WHEN 'buyQuantity' THEN buyQuantity " +
			"END ASC)) " + 
			"WHERE roworderNumber BETWEEN ?3 AND ?4";
	
	String sqlOrderNumberDESC =
			"SELECT roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount ,quantity " + 
			"FROM " + 
			"(SELECT ROWNUM AS roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount,quantity " + 
			"FROM " + 
			"(SELECT O.ORDER_ID AS orderID, O.ORDER_DATE AS orderDate, M.CUSTOMER_NAME AS customerName, G.GOODS_ID AS goodsID, G.GOODS_NAME AS goodsName, " + 
			"O.GOODS_BUY_PRICE AS goodsBuyPrice, O.BUY_QUANTITY AS buyQuantity, COUNT(O.ORDER_ID) OVER () AS orderCount ,G.QUANTITY as quantity " + 
			"FROM BEVERAGE_ORDER O " + 
			"JOIN BEVERAGE_MEMBER M ON O.CUSTOMER_ID = M.IDENTIFICATION_NO " + 
			"JOIN BEVERAGE_GOODS G ON O.GOODS_ID = G.GOODS_ID " + 
			"WHERE TRUNC(ORDER_DATE) BETWEEN TO_DATE(?1, 'yyyy/MM/dd') AND TO_DATE(?2, 'yyyy/MM/dd') " + 
			"ORDER BY CASE ?5 "+
			"WHEN 'orderID' THEN orderID " +
			"WHEN 'goodsID' THEN goodsID " +
			"WHEN 'goodsBuyPrice' THEN goodsBuyPrice " +
			"WHEN 'buyQuantity' THEN buyQuantity " +
			"END DESC)) " + 
			"WHERE roworderNumber BETWEEN ?3 AND ?4";
	
	String sqlOrderTextASC =
			"SELECT roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount ,quantity " + 
			"FROM " + 
			"(SELECT ROWNUM AS roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount ,quantity" + 
			"FROM " + 
			"(SELECT O.ORDER_ID AS orderID, O.ORDER_DATE AS orderDate, M.CUSTOMER_NAME AS customerName, G.GOODS_ID AS goodsID, G.GOODS_NAME AS goodsName, " + 
			"O.GOODS_BUY_PRICE AS goodsBuyPrice, O.BUY_QUANTITY AS buyQuantity, COUNT(O.ORDER_ID) OVER () AS orderCount ,G.QUANTITY as quantity " + 
			"FROM BEVERAGE_ORDER O " + 
			"JOIN BEVERAGE_MEMBER M ON O.CUSTOMER_ID = M.IDENTIFICATION_NO " + 
			"JOIN BEVERAGE_GOODS G ON O.GOODS_ID = G.GOODS_ID " + 
			"WHERE TRUNC(ORDER_DATE) BETWEEN TO_DATE(?1, 'yyyy/MM/dd') AND TO_DATE(?2, 'yyyy/MM/dd') " + 
			"ORDER BY CASE ?5 "+
			"WHEN 'customerName' THEN customerName " +
			"WHEN 'goodsName' THEN goodsName " +
			"END ASC)) " + 
			"WHERE roworderNumber BETWEEN ?3 AND ?4";
	

	
	String sqlOrderTextDESC =
			"SELECT roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount ,quantity" + 
			"FROM " + 
			"(SELECT ROWNUM AS roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount ,quantity " + 
			"FROM " + 
			"(SELECT O.ORDER_ID AS orderID, O.ORDER_DATE AS orderDate, M.CUSTOMER_NAME AS customerName, G.GOODS_ID AS goodsID, G.GOODS_NAME AS goodsName, " + 
			"O.GOODS_BUY_PRICE AS goodsBuyPrice, O.BUY_QUANTITY AS buyQuantity, COUNT(O.ORDER_ID) OVER () AS orderCount ,G.QUANTITY as quantity  " + 
			"FROM BEVERAGE_ORDER O " + 
			"JOIN BEVERAGE_MEMBER M ON O.CUSTOMER_ID = M.IDENTIFICATION_NO " + 
			"JOIN BEVERAGE_GOODS G ON O.GOODS_ID = G.GOODS_ID " + 
			"WHERE TRUNC(ORDER_DATE) BETWEEN TO_DATE(?1, 'yyyy/MM/dd') AND TO_DATE(?2, 'yyyy/MM/dd') " + 
			"ORDER BY CASE ?5 "+
			"WHEN 'customerName' THEN customerName " +
			"WHEN 'goodsName' THEN goodsName " +
			"END DESC)) " + 
			"WHERE roworderNumber BETWEEN ?3 AND ?4";
	// 這四個是查詢全部訂單
	@Query(value = sqlOrderNumberASC, nativeQuery = true)
	List<GoodsReportSales> queryGoodSalesNumberASC(String startDate, String endDate, long rowStart, long rowEnd,String orderByItem);
	@Query(value = sqlOrderNumberDESC, nativeQuery = true)
	List<GoodsReportSales> queryGoodSalesNumberDESC(String startDate, String endDate, long rowStart, long rowEnd,String orderByItem);
	@Query(value = sqlOrderTextASC, nativeQuery = true)
	List<GoodsReportSales> queryGoodSalesTextASC(String startDate, String endDate, long rowStart, long rowEnd,String orderByItem);
	@Query(value = sqlOrderTextDESC, nativeQuery = true)
	List<GoodsReportSales> queryGoodSalesTextDESC(String startDate, String endDate, long rowStart, long rowEnd,String orderByItem);

	
	
	String sqlOrderNumberASC2 =
			"SELECT roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount,quantity " + 
			"FROM " + 
			"(SELECT ROWNUM AS roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount ,quantity " + 
			"FROM " + 
			"(SELECT O.ORDER_ID AS orderID, O.ORDER_DATE AS orderDate, M.CUSTOMER_NAME AS customerName, G.GOODS_ID AS goodsID, G.GOODS_NAME AS goodsName, " + 
			"O.GOODS_BUY_PRICE AS goodsBuyPrice, O.BUY_QUANTITY AS buyQuantity, COUNT(O.ORDER_ID) OVER () AS orderCount ,G.QUANTITY as quantity " + 
			"FROM BEVERAGE_ORDER O " + 
			"JOIN BEVERAGE_MEMBER M ON O.CUSTOMER_ID = M.IDENTIFICATION_NO " + 
			"JOIN BEVERAGE_GOODS G ON O.GOODS_ID = G.GOODS_ID " + 
			"WHERE TRUNC(ORDER_DATE) BETWEEN TO_DATE(?1, 'yyyy/MM/dd') AND TO_DATE(?2, 'yyyy/MM/dd') " +
			"AND M.CUSTOMER_NAME = ?6 "+ 
			"ORDER BY CASE ?5 "+
			"WHEN 'orderID' THEN orderID " +
			"WHEN 'goodsID' THEN goodsID " +
			"WHEN 'goodsBuyPrice' THEN goodsBuyPrice " +
			"WHEN 'buyQuantity' THEN buyQuantity " +
			"END ASC)) " + 
			"WHERE roworderNumber BETWEEN ?3 AND ?4";
	
	String sqlOrderNumberDESC2 =
			"SELECT roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount, quantity " + 
			"FROM " + 
			"(SELECT ROWNUM AS roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount, quantity " + 
			"FROM " + 
			"(SELECT O.ORDER_ID AS orderID, O.ORDER_DATE AS orderDate, M.CUSTOMER_NAME AS customerName, G.GOODS_ID AS goodsID, G.GOODS_NAME AS goodsName, " + 
			"O.GOODS_BUY_PRICE AS goodsBuyPrice, O.BUY_QUANTITY AS buyQuantity, COUNT(O.ORDER_ID) OVER () AS orderCount ,G.QUANTITY as quantity " + 
			"FROM BEVERAGE_ORDER O " + 
			"JOIN BEVERAGE_MEMBER M ON O.CUSTOMER_ID = M.IDENTIFICATION_NO " + 
			"JOIN BEVERAGE_GOODS G ON O.GOODS_ID = G.GOODS_ID " + 
			"WHERE TRUNC(ORDER_DATE) BETWEEN TO_DATE(?1, 'yyyy/MM/dd') AND TO_DATE(?2, 'yyyy/MM/dd') " + 
			"AND M.CUSTOMER_NAME = ?6 "+ 
			"ORDER BY CASE ?5 "+
			"WHEN 'orderID' THEN orderID " +
			"WHEN 'goodsID' THEN goodsID " +
			"WHEN 'goodsBuyPrice' THEN goodsBuyPrice " +
			"WHEN 'buyQuantity' THEN buyQuantity " +
			"END DESC)) " + 
			"WHERE roworderNumber BETWEEN ?3 AND ?4";
	
	String sqlOrderTextASC2 =
			"SELECT roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount,quantity " + 
			"FROM " + 
			"(SELECT ROWNUM AS roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount,quantity " + 
			"FROM " + 
			"(SELECT O.ORDER_ID AS orderID, O.ORDER_DATE AS orderDate, M.CUSTOMER_NAME AS customerName, G.GOODS_ID AS goodsID, G.GOODS_NAME AS goodsName, " + 
			"O.GOODS_BUY_PRICE AS goodsBuyPrice, O.BUY_QUANTITY AS buyQuantity, COUNT(O.ORDER_ID) OVER () AS orderCount ,G.QUANTITY as quantity " + 
			"FROM BEVERAGE_ORDER O " + 
			"JOIN BEVERAGE_MEMBER M ON O.CUSTOMER_ID = M.IDENTIFICATION_NO " + 
			"JOIN BEVERAGE_GOODS G ON O.GOODS_ID = G.GOODS_ID " + 
			"WHERE TRUNC(ORDER_DATE) BETWEEN TO_DATE(?1, 'yyyy/MM/dd') AND TO_DATE(?2, 'yyyy/MM/dd') " + 
			"AND M.CUSTOMER_NAME = ?6 "+ 
			"ORDER BY CASE ?5 "+
			"WHEN 'customerName' THEN customerName " +
			"WHEN 'goodsName' THEN goodsName " +
			"END ASC)) " + 
			"WHERE roworderNumber BETWEEN ?3 AND ?4";
	
	
	
	
	
	String sqlOrderTextDESC2 =
			"SELECT roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount ,quantity " + 
			"FROM " + 
			"(SELECT ROWNUM AS roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount ,quantity " + 
			"FROM " + 
			"(SELECT O.ORDER_ID AS orderID, O.ORDER_DATE AS orderDate, M.CUSTOMER_NAME AS customerName, G.GOODS_ID AS goodsID, G.GOODS_NAME AS goodsName, " + 
			"O.GOODS_BUY_PRICE AS goodsBuyPrice, O.BUY_QUANTITY AS buyQuantity, COUNT(O.ORDER_ID) OVER () AS orderCount ,G.QUANTITY as quantity " + 
			"FROM BEVERAGE_ORDER O " + 
			"JOIN BEVERAGE_MEMBER M ON O.CUSTOMER_ID = M.IDENTIFICATION_NO " + 
			"JOIN BEVERAGE_GOODS G ON O.GOODS_ID = G.GOODS_ID " + 
			"WHERE TRUNC(ORDER_DATE) BETWEEN TO_DATE(?1, 'yyyy/MM/dd') AND TO_DATE(?2, 'yyyy/MM/dd') " + 
			"AND M.CUSTOMER_NAME = ?6 "+ 
			"ORDER BY CASE ?5 "+
			"WHEN 'customerName' THEN customerName " +
			"WHEN 'goodsName' THEN goodsName " +
			"END DESC)) " + 
			"WHERE roworderNumber BETWEEN ?3 AND ?4";
	
	//下面四個是查詢會員個人訂單
	@Query(value = sqlOrderNumberASC2, nativeQuery = true)
	List<GoodsReportSales> queryMemberOrderNumberASC(String startDate, String endDate, long rowStart, long rowEnd,String orderByItem,String cusName);
	@Query(value = sqlOrderNumberDESC2, nativeQuery = true)
	List<GoodsReportSales> queryMemberOrderNumberDESC(String startDate, String endDate, long rowStart, long rowEnd,String orderByItem,String cusName);
	@Query(value = sqlOrderTextASC2, nativeQuery = true)
	List<GoodsReportSales> queryMemberOrderTextASC(String startDate, String endDate, long rowStart, long rowEnd,String orderByItem,String cusName);
	@Query(value = sqlOrderTextDESC2, nativeQuery = true)
	List<GoodsReportSales> queryMemberOrderTextDESC(String startDate, String endDate, long rowStart, long rowEnd,String orderByItem,String cusName);

	/* 這邊是個人訂單在SQL測試使用的:(有多了quantity是為了給前端判別是不是還可以再買一次)
	 * SELECT roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount ,quantity
		FROM 
		(SELECT ROWNUM AS roworderNumber, orderID, orderDate, customerName, goodsID, goodsName, goodsBuyPrice, buyQuantity, orderCount ,quantity
		FROM 
		(SELECT O.ORDER_ID AS orderID, O.ORDER_DATE AS orderDate, M.CUSTOMER_NAME AS customerName, G.GOODS_ID AS goodsID, G.GOODS_NAME AS goodsName,  
		O.GOODS_BUY_PRICE AS goodsBuyPrice, O.BUY_QUANTITY AS buyQuantity, COUNT(O.ORDER_ID) OVER () AS orderCount ,G.QUANTITY as quantity
		FROM BEVERAGE_ORDER O 
		JOIN BEVERAGE_MEMBER M ON O.CUSTOMER_ID = M.IDENTIFICATION_NO 
		JOIN BEVERAGE_GOODS G ON O.GOODS_ID = G.GOODS_ID 
		WHERE TRUNC(ORDER_DATE) BETWEEN TO_DATE('2023-04-28') AND TO_DATE('2023-05-28') 
		AND M.CUSTOMER_NAME = 'Jay'
		ORDER BY CASE 'goodsID' 
		WHEN 'orderID' THEN orderID 
		WHEN 'goodsID' THEN goodsID 
		WHEN 'goodsBuyPrice' THEN goodsBuyPrice 
		WHEN 'buyQuantity' THEN buyQuantity 
		END ASC)) 
		WHERE roworderNumber BETWEEN 1 AND 100 ;
	 * */
	
	
	
/*	 
	//單獨使用orderID方式排序
		@Query(value=
				"WITH ORDER_Condition AS (\r\n" + 
				"SELECT ROWNUM roworderNumber, O.ORDER_ID orderID, O.ORDER_DATE orderDate, M.CUSTOMER_NAME customerName, " + 
				"G.GOODS_ID goodsID, G.GOODS_NAME goodsName, " + 
				"O.GOODS_BUY_PRICE goodsBuyPrice, O.BUY_QUANTITY buyQuantity, " + 
				"COUNT(O.ORDER_ID) OVER () orderCount " + 
				"FROM BEVERAGE_ORDER O  " + 
				"JOIN BEVERAGE_MEMBER M ON O.CUSTOMER_ID = M.IDENTIFICATION_NO " + 
				"JOIN BEVERAGE_GOODS G ON O.GOODS_ID = G.GOODS_ID  " + 
				"WHERE TRUNC(ORDER_DATE) BETWEEN TO_DATE(?1, 'yyyy/MM/dd') AND TO_DATE(?2, 'yyyy/MM/dd') " + 
				//SQL 無法再orderBy直接使用參數 需要動態組
				"ORDER BY CASE ?5 " +
				"    WHEN 'goodsID' THEN goodsID " +
				"    WHEN 'goodsBuyPrice' THEN goodsBuyPrice " +
				"    WHEN 'buyQuantity' THEN buyQuantity " +
//				"    WHEN 'orderDate' THEN orderDate " +
//				"    WHEN 'customerName' THEN customerName " +
//				"    WHEN 'goodsName' THEN goodsName " +
				"    ELSE orderID " +
				"END DESC )" + 

				
//				"ORDER BY ?5 )" + 
				"SELECT * FROM ORDER_Condition " + 
				"WHERE roworderNumber BETWEEN ?3 AND ?4",
				nativeQuery = true)
		List<GoodsReportSales> queryGoodSales(String startDate, String endDate, long rowStart, long rowEnd,String orderByItem);
	*/	

}
