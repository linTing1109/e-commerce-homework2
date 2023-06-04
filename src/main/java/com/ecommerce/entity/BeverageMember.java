package com.ecommerce.entity;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
@Entity
@Table(name = "BEVERAGE_MEMBER", schema="LOCAL")
public class BeverageMember {
	@Id
	@Column(name = "IDENTIFICATION_NO")
	private String identificationNo;//會員帳號
	
	@Column(name = "PASSWORD")
	private String password;//會員密碼
	
	@Column(name = "CUSTOMER_NAME")
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
