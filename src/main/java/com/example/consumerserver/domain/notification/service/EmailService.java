package com.example.consumerserver.domain.notification.service;

import java.util.List;

import com.example.consumerserver.domain.notification.dto.SendEmailMessageRequest;

public interface EmailService {
	void sendAll(List<SendEmailMessageRequest> requests);
}
