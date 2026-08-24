package com.example.consumerserver.domain.notification.dto;

public record SendEmailMessageRequest(

	// 받는 사람
	String to,

	// 제목
	String subject,

	// 메세지
	String message
) {
}
