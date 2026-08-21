package com.example.consumerserver.domain.notification.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${test.simulate-email-delay:false}")
	private boolean simulateEmailDelay;

	@Override
	public void send(SendEmailMessageRequest request) {
		sendAll(List.of(request));
	}

	@Override
	public void sendAll(List<SendEmailMessageRequest> requests) {
		if (requests.isEmpty()) return;

		long createStart = System.currentTimeMillis();
		MimeMessage[] messages = requests.stream()
			.map(this::createMimeMessage)
			.toArray(MimeMessage[]::new);
		long createTime = System.currentTimeMillis() - createStart;

		long sendStart = System.currentTimeMillis();

		if (simulateEmailDelay) {
			long simulatedDelay = 6414 + 6739L * requests.size();
			try {
				Thread.sleep(simulatedDelay);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException("이메일 발송 시뮬레이션 중단", e);
			}
		} else {
			javaMailSender.send(messages);
		}

		long sendTime = System.currentTimeMillis() - sendStart;

		log.info("[이메일 배치] 건수:{} 생성:{}ms 전송:{}ms 건당평균:{}ms",
			requests.size(), createTime, sendTime, sendTime / requests.size());
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
