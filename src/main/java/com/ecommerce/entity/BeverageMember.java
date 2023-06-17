package com.ecommerce.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

//lombok部分
@SuperBuilder //允許預設建構式 也可以寫建構者模式 (不使用Builder是因為這樣外面就不可直接使用 ex:findAll背後有預設建構者)
@NoArgsConstructor //無參數建構式
@Data //lombok使用

@Entity //標註類別為一個資料庫實體
@Table(name = "BEVERAGE_MEMBER", schema="LOCAL") //設置實際對應的資料表名稱及來源 schema
public class BeverageMember {
	@Id //主鍵
	@Column(name = "IDENTIFICATION_NO") //對應資料表的欄位名:IDENTIFICATION_NO
	private String identificationNo;//會員帳號
	
	@Column(name = "PASSWORD") //對應資料表的欄位名:PASSWORD
	private String password;//會員密碼
	
	@Column(name = "CUSTOMER_NAME") //對應資料表的欄位名:CUSTOMER_NAME
	private String customerName;//會員名稱
	
//	// 避免聯集查詢被動觸發遞迴查尋的問題(java.lang.StackOverflowError)
//	@JsonIgnore
//	// 雙向一對多關係(物件雙方各自都擁有對方的實體參照，雙方皆意識到對方的存在)
//	@OneToMany(
//		// 透過mappedBy來連結雙向之間的關係，所對映的物件欄位名稱(除非關係是單向的，否則此參數必須)
//		mappedBy = "IDENTIFICATION_NO",
//		cascade = {CascadeType.ALL}, orphanRemoval = true,
//		fetch = FetchType.LAZY // LAZY(只在用到時才載入關聯的物件)
//	)
//
//	private List<BeverageOrder> beverageOrders;
}
