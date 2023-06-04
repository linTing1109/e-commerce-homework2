package com.ecommerce.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@ToString
public class MemberInfoVo {
	private Boolean isLogin;//登入狀態
	private String loginMessage;//登入訊息
	private String identificationNo;//帳號
	private String cusName;//姓名
	private String cusPassword;//密碼
}
