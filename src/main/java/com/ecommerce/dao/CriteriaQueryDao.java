package com.ecommerce.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.ecommerce.entity.BeverageGoods;
import com.ecommerce.entity.BeverageMember;
import com.ecommerce.entity.BeverageOrder;
import com.ecommerce.vo.GenericPageable;
import com.ecommerce.vo.GoodsDataCondition;
import com.ecommerce.vo.GoodsReportSales;

@Repository
public class CriteriaQueryDao { //自己寫criteria 所以就不需要繼承
	//受到JPA控管的物件
	@PersistenceContext(name = "oracleEntityManager")//為config/OracleDataSourceConfig 那邊Bean設定的名字
    private EntityManager entityManager;//這樣才知道要用哪個資料庫
	
	public List<BeverageGoods> queryGoodsConditions(GoodsDataCondition condition,GenericPageable genericPageable){
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();//查詢建立器
        CriteriaQuery<BeverageGoods> cq = cb.createQuery(BeverageGoods.class);//建立一個查詢 相當於select 欄位
        Root<BeverageGoods> beverageGoods = cq.from(BeverageGoods.class);//from 欄位
        
        
        List<Predicate> predicates = new ArrayList<>();//Predicate物件(java8)
        if (condition.getGoodsID() != null) {
        	predicates.add(cb.equal(beverageGoods.get("goodsID"), condition.getGoodsID()));
        }
        if(condition.getGoodsName() !=null) {
        	//忽略前後位置 忽略大小寫
        	String goodName="%"+condition.getGoodsName().trim()+"%";
        	predicates.add(cb.like(cb.lower(beverageGoods.get("goodsName")), goodName.toLowerCase()));
        }
        
        if(condition.getStartPrice() !=null ) {
        	predicates.add(cb.greaterThanOrEqualTo(beverageGoods.get("goodsPrice"), condition.getStartPrice()));
        }
        
        if(condition.getEndPrice() !=null ) {
        	predicates.add(cb.lessThanOrEqualTo(beverageGoods.get("goodsPrice"), condition.getEndPrice()));
        }
        
        if(condition.getQuantity() !=null) {
        	predicates.add(cb.lessThanOrEqualTo(beverageGoods.get("goodsQuantity"), condition.getQuantity()));
        }
        
        if(condition.getStatus() !=null) {
        	
        	predicates.add(cb.equal(beverageGoods.get("status"),Integer.parseInt(condition.getStatus())));
        }
        if(condition.getSort() != null) {
       	 if ("asc".equalsIgnoreCase(condition.getSort())) {
       		cq.orderBy(cb.asc(beverageGoods.get(condition.getOrderByItem())));//依照輸入的排序條件
//       		 cq.orderBy(cb.asc(beverageGoods.get("goodsPrice")));
            } else if ("desc".equalsIgnoreCase(condition.getSort())) {
            	cq.orderBy(cb.desc(beverageGoods.get(condition.getOrderByItem())));	//依照輸入的排序條件
//           	 cq.orderBy(cb.desc(beverageGoods.get("goodsPrice")));
            }
       }
//        cq.select(beverageGoods);//select good 這行可以省略不寫
        Predicate[] restriction = predicates.toArray(new Predicate[0]);//組合查詢條件
        
        //這邊只是在計算總筆數:並非最後畫面呈現的(這三行應該可以拿掉)
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(BeverageGoods.class)));
        countQuery.where(restriction);
        
        cq.where(restriction);//放入全部查詢條件
        TypedQuery<BeverageGoods> query = entityManager.createQuery(cq);//執行查詢
        genericPageable.setDataTotalSize(query.getResultList().size());//資料總筆數
        
        int offset=(int) ((genericPageable.getCurrentPageNo()-1) * genericPageable.getPageDataSize()); //偏移量(起始的位置) 頁碼*每頁總數
		int limit=genericPageable.getPageDataSize();//每頁顯示的筆數
        query.setFirstResult(offset);//偏移量(起始的位置)  
        query.setMaxResults(limit);//每頁總共塞幾筆
        
		return query.getResultList();
	}

	public List<BeverageGoods> queryGoodsKeyword(String searchKeyword,GenericPageable genericPageable){
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<BeverageGoods> cq = cb.createQuery(BeverageGoods.class);
		Root<BeverageGoods> beverageGoods = cq.from(BeverageGoods.class);
		
		List<Predicate> predicates = new ArrayList<>();
		if(searchKeyword !=null) {
			//忽略前後位置 忽略大小寫
			String goodName="%"+searchKeyword.trim()+"%";
			predicates.add(cb.like(cb.lower(beverageGoods.get("goodsName")), goodName.toLowerCase()));
		}
		//不管有沒有搜尋文字 都應該是搜尋出來上架 (目前沒庫存的也秀出來)
			predicates.add(cb.equal(beverageGoods.get("status"),"1"));//因為是在前台 所以只秀出上架的商品
//			predicates.add(cb.greaterThan(beverageGoods.get("goodsQuantity"),0));//商品庫存大於0才會秀出來 (0也不顯示)
		
		cq.orderBy(cb.asc(beverageGoods.get("goodsID")));
		Predicate[] restriction = predicates.toArray(new Predicate[0]);//組合查詢條件
		
		//這邊只是在計算總筆數:並非畫面呈現用
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		countQuery.select(cb.count(countQuery.from(BeverageGoods.class)));
		countQuery.where(restriction);
		
		cq.where(restriction);//放入全部查詢條件
		TypedQuery<BeverageGoods> query = entityManager.createQuery(cq);
		genericPageable.setDataTotalSize(query.getResultList().size());//資料總筆數
		
		int offset=(int) ((genericPageable.getCurrentPageNo()-1) * genericPageable.getPageDataSize()); //偏移量(起始的位置) 頁碼*每頁總數
		int limit=genericPageable.getPageDataSize();//每頁顯示的筆數
		query.setFirstResult(offset);//偏移量(起始的位置)  
		query.setMaxResults(limit);//每頁總共塞幾筆
		
		return query.getResultList();
	}
	
	
//	public List<GoodsReportSales> queryGeographyJoinStore(LocalDateTime localDateStart,LocalDateTime localDateEnd) {
//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<GoodsReportSales> cq = cb.createQuery(GoodsReportSales.class); //合併完的表格
//                
//        Root<BeverageOrder> geography = cq.from(BeverageOrder.class);//主表格
//        // Spring JPA 不支援 RIGHT JOIN //joinType:inner,left,right
//        Join<BeverageOrder, BeverageMember> join = geography.join("beverageMember", JoinType.INNER);//g表內有storeInfos
//        Join<BeverageOrder, BeverageGoods> join2 = geography.join("beverageGoods", JoinType.INNER);//g表內有storeInfos
//        cq.multiselect(
//			//本來geography欄位
//	      	geography.get("geographyID"), geography.get("regionName"),
//			//join store欄位
//	      	join.get("storeID"), join.get("storeName"), 
//	      	join.get("sales"), join.get("storeDate")	      	
//        );
//        
//        // 執行查詢
//        TypedQuery<GeographyJoinStore> query = entityManager.createQuery(cq);
//        
//        return query.getResultList();
//	}
	
//	public List<GoodsReportSales> queryOrderConditions(LocalDateTime localDateStart,LocalDateTime localDateEnd){
//		CriteriaBuilder criteriaBuilder  = entityManager.getCriteriaBuilder();//獲取 CriteriaBuilder 物件
//        CriteriaQuery<GoodsReportSales> cq = criteriaBuilder.createQuery(GoodsReportSales.class);
//        Root<BeverageOrder> orderRoot  = cq.from(BeverageOrder.class);
//        Join<BeverageOrder, BeverageMember> memberJoin = orderRoot.join("beverageMember", JoinType.INNER);//beverageOrder內的名稱
//        Join<BeverageOrder, BeverageGoods> goodsJoin = orderRoot.join("beverageGoods", JoinType.INNER);
//        
//        cq.multiselect(
//    			//本來geography欄位
//        		orderRoot.get("orderID"), 
//        		orderRoot.get("orderDate"),
//        		orderRoot.get("goodsByPrice"),
//        		orderRoot.get("buyQuantity"),
//    			//join store欄位
//        		memberJoin.get("customerName"),
//        		goodsJoin.get("goodsName") 	      	
//            );
//     // 執行查詢
//      TypedQuery<GoodsReportSales> query = entityManager.createQuery(cq);
////        ParameterExpression<String> param1 = criteriaBuilder.parameter(String.class);
////        ParameterExpression<String> param2 = criteriaBuilder.parameter(String.class);
////        ParameterExpression<String> param3 = criteriaBuilder.parameter(String.class);
////
////        cq.select(
////        	    criteriaBuilder.construct(
////        	    	GoodsReportSales.class,
////        	        orderRoot.get("orderId"),
////        	        memberJoin.get("customerName"),
////        	        orderRoot.get("orderDate"),
////        	        goodsJoin.get("goodsName"),
////        	        orderRoot.get("goodsBuyPrice"),
////        	        orderRoot.get("buyQuantity")
////        	    )
////        	).where(
////        	    criteriaBuilder.and(
////        	        criteriaBuilder.equal(orderRoot.get("customerId"), memberJoin.get("identificationNo")),
////        	        criteriaBuilder.equal(orderRoot.get("goodsId"), goodsJoin.get("goodsId")),
////        	        criteriaBuilder.between(
////        	            orderRoot.get("orderDate"),
////        	            criteriaBuilder.literal(Timestamp.valueOf(param1 + " 00:00:00")),
////        	            criteriaBuilder.literal(Timestamp.valueOf(param2 + " 23:59:59"))
////        	        ),
////        	        criteriaBuilder.equal(orderRoot.get("customerId"), param3)
////        	    )
////        	).orderBy(
////        	    criteriaBuilder.asc(orderRoot.get("orderId"))
////        	);
////        TypedQuery<GoodsReportSales> typedQuery = entityManager.createQuery(query);
////        typedQuery.setParameter("yyyy/MM/dd", TemporalType.DATE);
////        typedQuery.setParameter("yyyy/MM/dd", TemporalType.DATE);
////        typedQuery.setParameter(param3, "CUSTOMER_ID_VALUE");
////
////        List<GoodsReportSales> results = typedQuery.getResultList();
//////        
//        //這邊只是在計算總筆數:並非最後畫面呈現的
//        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
////        countQuery.select(criteriaBuilder.count(countQuery.from(BeverageGoods.class)));
////        countQuery.where(restriction);
//        
////        cq.where(restriction);//放入全部查詢條件
////        TypedQuery<BeverageGoods> query = entityManager.createQuery(cq);
////        genericPageable.setDataTotalSize(query.getResultList().size());//資料總筆數
////        
////        int offset=(int) ((genericPageable.getCurrentPageNo()-1) * genericPageable.getPageDataSize()); //偏移量(起始的位置) 頁碼*每頁總數
////		int limit=genericPageable.getPageDataSize();//每頁顯示的筆數
////        query.setFirstResult(offset);//偏移量(起始的位置)  
////        query.setMaxResults(limit);//每頁總共塞幾筆
//        
//		return query.getResultList();
//	}
}
