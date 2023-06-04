package com.ecommerce.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.service.MailService;
import com.ecommerce.vo.CheckoutCompleteInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;


//跨站請求
//對應 React axios 設置 {withCredentials: true} HttpHeader就能帶有Cookie夾帶JESSIONID 
//Server後端必須設置 Access-Control-Allow-Origin(不能為*)、Access-Control-Allow-Credentials(必須為true)
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:8090"}, allowCredentials = "true")
@RestController
@RequestMapping("/ecommerce/MailController")
public class MailController {
  @Autowired
 private MailService mailService;//郵件服務的實例

 //目前純文字的成功
 @ApiOperation(value = "購物網-mail-發送")
 @GetMapping("/sendMailText")
 @ResponseBody
 public String hello(@RequestParam String mailAddress,@RequestParam String message,@RequestParam String messageText){
  String result=mailService.prepareAndSend(mailAddress,message,messageText);
//  mailService.prepareAndSend("tina771109@gmail.com","訂單完成","123");
  return result ;
 }
 
 
 
}
