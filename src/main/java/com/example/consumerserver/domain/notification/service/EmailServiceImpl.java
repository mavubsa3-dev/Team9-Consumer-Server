package com.example.consumerserver.domain.notification.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.consumerserver.domain.notification.dto.SendEmailMessageRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender javaMailSender;

	@Override
	public void send(SendEmailMessageRequest sendEmailMessageRequest){

		MimeMessage message = javaMailSender.createMimeMessage();

		try {

			MimeMessageHelper messageHelper = new MimeMessageHelper(message, false, "UTF-8");
			messageHelper.setTo(sendEmailMessageRequest.to());
			messageHelper.setSubject(sendEmailMessageRequest.subject());
			messageHelper.setText(sendEmailMessageRequest.message(), true);

			javaMailSender.send(message);

			log.info("[이메일 전송] : {} ", sendEmailMessageRequest.to());
		} catch (MessagingException e){
			throw new RuntimeException("이메일 전송 중 오류 발생", e);
		}
	}
}
