package com.ecommerce.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
@ToString 
@EqualsAndHashCode(of = {"orderID"})
@Entity
@Table(name = "BEVERAGE_ORDER", schema="LOCAL")
public class BeverageOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BEVERAGE_ORDER_SEQ_GEN")
    @SequenceGenerator(name = "BEVERAGE_ORDER_SEQ_GEN", sequenceName = "BEVERAGE_ORDER_SEQ", allocationSize = 1)
	@Column(name = "ORDER_ID")
	private long orderID;//訂單編號
	
	@Column(name = "ORDER_DATE")
	private LocalDateTime orderDate;//訂單日期
	
	@Column(name = "CUSTOMER_ID")
	private String customerID;//顧客ID
	
	@Column(name = "GOODS_BUY_PRICE")
	private long goodsByPrice;//買的價錢
	
	@Column(name = "BUY_QUANTITY")
	private long buyQuantity;//買的數量
	
	@Column(name = "GOODS_ID")
	private long goodsID;//商品ID
	
////	@JsonIgnore
//	@ManyToOne(fetch = FetchType.EAGER) // EAGER(在查詢時立刻載入關聯的物件)
//	@JoinColumn(name = "GOODS_ID", insertable = false, updatable = false)
//	private BeverageGoods beverageGoods;
	
//	@ManyToOne(fetch = FetchType.EAGER) // EAGER(在查詢時立刻載入關聯的物件)
//	@JoinColumn(name = "CUSTOMER_ID", insertable = false, updatable = false)
//	private BeverageMember beverageMember;
}
