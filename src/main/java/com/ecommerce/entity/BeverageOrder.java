package com.ecommerce.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

//lombok部分
@SuperBuilder  //允許預設建構式 也可以寫建構者模式 (不使用Builder是因為這樣外面就不可直接使用 ex:findAll背後有預設建構者)
@NoArgsConstructor //無參數建構式
@Data //lombok使用
@ToString  //生成toString 
@EqualsAndHashCode(of = {"orderID"}) //自動生成equals & hashCode 只考慮orderID變數

@Entity //標註類別為一個資料庫實體
@Table(name = "BEVERAGE_ORDER", schema="LOCAL") //設置實際對應的資料表名稱及來源 schema
public class BeverageOrder {
	
	@Id //主鍵
	//定義生成為序列SEQUENCE,生成器generator名稱
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BEVERAGE_ORDER_SEQ_GEN")
	//生成器名稱,序列的名稱,每次加1
    @SequenceGenerator(name = "BEVERAGE_ORDER_SEQ_GEN", sequenceName = "BEVERAGE_ORDER_SEQ", allocationSize = 1)
	@Column(name = "ORDER_ID") //對應資料表的欄位名:ORDER_ID
	private long orderID;//訂單編號
	
	@Column(name = "ORDER_DATE") //對應資料表的欄位名:ORDER_DATE
	private LocalDateTime orderDate;//訂單日期
	
	@Column(name = "CUSTOMER_ID") //對應資料表的欄位名:CUSTOMER_ID
	private String customerID;//顧客ID
	
	@Column(name = "GOODS_BUY_PRICE") //對應資料表的欄位名:GOODS_BUY_PRICE
	private long goodsByPrice;//買的價錢
	
	@Column(name = "BUY_QUANTITY") //對應資料表的欄位名:BUY_QUANTITY
	private long buyQuantity;//買的數量
	
	@Column(name = "GOODS_ID") //對應資料表的欄位名:GOODS_ID
	private long goodsID;//商品ID
	
////	@JsonIgnore
//	@ManyToOne(fetch = FetchType.EAGER) // EAGER(在查詢時立刻載入關聯的物件)
//	@JoinColumn(name = "GOODS_ID", insertable = false, updatable = false)
//	private BeverageGoods beverageGoods;
	
//	@ManyToOne(fetch = FetchType.EAGER) // EAGER(在查詢時立刻載入關聯的物件)
//	@JoinColumn(name = "CUSTOMER_ID", insertable = false, updatable = false)
//	private BeverageMember beverageMember;
}
