package com.ecommerce.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import com.ecommerce.vo.GoodsVo;
import com.ecommerce.vo.MemberInfo;

@Configuration //SpringBoot專案 標記類為配置類
public class MemberConfig {
	
	@Bean(name="member")
	@SessionScope //可跨HttpRequest 之間存取,範圍HTTP session
	public MemberInfo sessionMemberInfo(){ //會員相關資料
		return MemberInfo.builder().isLogin(false).build();
	}
	
	
	@Bean
	@SessionScope //可跨HttpRequest 之間存取,範圍HTTP session
	public List<GoodsVo> sessionCartGoods(){ //購物車
		return new ArrayList<GoodsVo>();
	}
}
