package com.example.consumerserver.domain.notification.service;

import com.example.consumerserver.domain.notification.dto.SendEmailMessageRequest;

public interface EmailService {

	void send(SendEmailMessageRequest sendEmailMessageRequest);
}
