package com.example.consumerserver.common.config.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommonListener {

	private final NotificationService notificationService;

	@KafkaListener(
		topics = "payment-completed",
		groupId = "payment-completed-notification",
		containerFactory = "paymentCompletedNotificationEventKafkaListenerContainerFactory"
	) public void consume(PaymentCompletedEvent event){

		log.info("[알림 처리] : paymentId : {} ", event.paymentId());
		notificationService.saveAndSend(event);
	}
}
