package com.ecommerce.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
@ToString (exclude = {"beverageOrders"})
@Entity
@EqualsAndHashCode(of = {"goodsID"})
@Table(name = "BEVERAGE_GOODS", schema="LOCAL")
public class BeverageGoods { 
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BEVERAGE_GOODS_SEQ_GEN")
    @SequenceGenerator(name = "BEVERAGE_GOODS_SEQ_GEN", sequenceName = "BEVERAGE_GOODS_SEQ", allocationSize = 1)
	@Column(name = "GOODS_ID")
	private long goodsID;//商品編號
	
	@Column(name = "GOODS_NAME")
	private String goodsName;//商品名稱
	
	@Column(name = "DESCRIPTION")
	private String description;//商品描述
	
	@Column(name = "PRICE")
	private int goodsPrice;//商品價格
	
	@Column(name = "QUANTITY")
	private int goodsQuantity;//商品庫存
	
	@Column(name = "IMAGE_NAME")
	private String goodsImageName;//商品圖片名稱
	
	@Column(name = "STATUS")
	private String status;//商品狀態 1:上架 0:下架
	
	// 避免聯集查詢被動觸發遞迴查尋的問題(java.lang.StackOverflowError)
	@JsonIgnore
	// 雙向一對多關係(物件雙方各自都擁有對方的實體參照，雙方皆意識到對方的存在)
	@OneToMany(
		// 透過mappedBy來連結雙向之間的關係，所對映的物件欄位名稱(除非關係是單向的，否則此參數必須)
		mappedBy = "goodsID",
		cascade = {CascadeType.ALL}, orphanRemoval = true,
		fetch = FetchType.LAZY // LAZY(只在用到時才載入關聯的物件)
	)

	private List<BeverageOrder> beverageOrders;
	
}
