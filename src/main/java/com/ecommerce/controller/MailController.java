package com.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.ecommerce.service.MailService;


import io.swagger.annotations.ApiOperation;

//跨站請求
//對應 React axios 設置 {withCredentials: true} HttpHeader就能帶有Cookie夾帶JESSIONID 
//Server後端必須設置 Access-Control-Allow-Origin(不能為*)、Access-Control-Allow-Credentials(必須為true)
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:8090" }, allowCredentials = "true")
@RestController // 標註於Controller類別上 等於@Controller+@ResponseBody(JSON)
@RequestMapping("/ecommerce/MailController") // 決定所有API共同的前綴fprefixesURL
public class MailController {
	
	@Autowired //透過Autowired的方式注入不同的實作
	private MailService mailService;// 郵件服務的實例

	// 目前純文字的成功 有圖片的失敗
	@ApiOperation(value = "購物網-mail-發送") //@ApiOperation描述API功能說明
	@GetMapping("/sendMailText") //@GetMapping:對應http GET 資料查詢
	@ResponseBody
	public String hello(@RequestParam String mailAddress, @RequestParam String message,
			@RequestParam String messageText) {
		//@RequestParam:用於Controller method參數上 取得http請求parameter帶入方法參數
		
		String result = mailService.prepareAndSend(mailAddress, message, messageText);
		//  mailService.prepareAndSend("xxxx@gmail.com","訂單完成","123"); //直接發送方式
		return result;
	}

}
