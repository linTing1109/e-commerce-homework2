package com.ecommerce.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import com.ecommerce.vo.GoodsVo;
import com.ecommerce.vo.MemberInfo;

@Configuration
public class MemberConfig {
	@Bean(name="member")
	@SessionScope
	public MemberInfo sessionMemberInfo(){
		return MemberInfo.builder().isLogin(false).build();
	}
	
	@Bean
	@SessionScope
	public List<GoodsVo> sessionCartGoods(){
		return new ArrayList<GoodsVo>();
	}
}
