package com.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class MailService {

	private JavaMailSender mailSender;

	// 透過Autowired的方式注入不同的實作
	@Autowired
	public MailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	// 純文字版本(可用於新增會員成功)
	public String prepareAndSend(String recipient, String message, String messageText) {
		String result = "";
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			// messageHelper可以設置郵件的發件人、收件人、主題和內容
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom("javaStudy1109@gmail.com");// 發件人
			messageHelper.setTo(recipient);// 收件人
			messageHelper.setSubject(message);// 主題
			messageHelper.setText(messageText);// 內容

		};
		try {
			mailSender.send(messagePreparator);
			result = "Mail sent ok!";

		} catch (MailException e) {
			result = "Mail sent error!";

		}
		return result;
	}

}
