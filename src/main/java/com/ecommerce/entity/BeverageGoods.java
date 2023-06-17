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

//lombok部分
@SuperBuilder //允許預設建構式 也可以寫建構者模式 (不使用Builder是因為這樣外面就不可直接使用 ex:findAll背後有預設建構者)
@NoArgsConstructor //無參數建構式
@Data  //lombok使用
@ToString (exclude = {"beverageOrders"}) //生成toString 但是排除beverageOrders
@EqualsAndHashCode(of = {"goodsID"}) //自動生成equals & hashCode 只考慮goodsID變數

@Entity //標註類別為一個資料庫實體
@Table(name = "BEVERAGE_GOODS", schema="LOCAL") //設置實際對應的資料表名稱及來源 schema
public class BeverageGoods {
	
	@Id  //主鍵
	//定義生成為序列SEQUENCE,生成器generator名稱
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BEVERAGE_GOODS_SEQ_GEN")
	//生成器名稱,序列的名稱,每次加1
    @SequenceGenerator(name = "BEVERAGE_GOODS_SEQ_GEN", sequenceName = "BEVERAGE_GOODS_SEQ", allocationSize = 1)
	@Column(name = "GOODS_ID") //對應資料表的欄位名:GOODS_ID
	private long goodsID;//商品編號
	
	@Column(name = "GOODS_NAME") //對應資料表的欄位名:GOODS_NAME
	private String goodsName;//商品名稱
	
	@Column(name = "DESCRIPTION") //對應資料表的欄位名:DESCRIPTION
	private String description;//商品描述
	
	@Column(name = "PRICE") //對應資料表的欄位名:PRICE
	private int goodsPrice;//商品價格
	
	@Column(name = "QUANTITY") //對應資料表的欄位名:QUANTITY
	private int goodsQuantity;//商品庫存
	
	@Column(name = "IMAGE_NAME") //對應資料表的欄位名:IMAGE_NAME
	private String goodsImageName;//商品圖片名稱
	
	@Column(name = "STATUS") //對應資料表的欄位名:STATUS
	private String status;//商品狀態 1:上架 0:下架
	
	//-----------------------------------------------------------------------------------------------
	//下面的部分是在練習雙向一對多關係,實際專題並沒有使用到
	
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
