package com.ecommerce.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(value= {
		"targetSource","beanExpressionResolver","advisors","classFilter",//序列化InvalidDefinitionException&InvalidDefinitionException
		"exposeProxy","preFiltered","targetClass","proxiedInterfaces",
		"proxyTargetClass","targetObject","frozen" //這七項是忽略顯示在swagger上面
})
public class MemberInfo {
	private Boolean isLogin;//登入狀態
	private String loginMessage;//登入訊息
	private String identificationNo;//帳號
	private String cusName;//姓名
	private String cusPassword;//密碼
}
