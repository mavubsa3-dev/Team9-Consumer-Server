package com.example.consumerserver.domain.notification.service;

import java.util.List;
import java.util.Map;

import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.example.consumerserver.domain.notification.dto.SendEmailMessageRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender javaMailSender;

	@Override
	public void sendAll(List<SendEmailMessageRequest> requests) {
		if (requests.isEmpty()) return;


		MimeMessage[] messages = requests.stream()
			.map(this::createMimeMessage)
			.toArray(MimeMessage[]::new);


		try {
			javaMailSender.send(messages);
		} catch (MailSendException e) {
			Map<Object, Exception> failedMessages = e.getFailedMessages();
			log.warn("이메일 일부 발송 실패, 실패 건수: {}", failedMessages.size());
		}
	}

	private MimeMessage createMimeMessage(SendEmailMessageRequest request) {
		MimeMessage message = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
			helper.setTo(request.to());
			helper.setSubject(request.subject());
			helper.setText(request.message(), true);
		} catch (MessagingException e) {
			throw new RuntimeException("이메일 메시지 생성 실패: " + request.to(), e);
		}
		return message;
	}
}
