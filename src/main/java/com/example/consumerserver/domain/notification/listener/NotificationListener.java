package com.example.consumerserver.domain.notification.listener;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

	private final NotificationService notificationService;

	@KafkaListener(
		topics = "payment-completed",
		groupId = "payment-completed-notification",
		containerFactory = "paymentCompletedNotificationEventKafkaListenerContainerFactory",
		batch = "true"
	) public void consumeNotification(List<PaymentCompletedEvent> events){

		long start = System.currentTimeMillis();

		notificationService.saveNotifications(events);
		notificationService.sendEmails(events);

		long time = System.currentTimeMillis() - start;
		log.info("[알림 배치 처리] 건수:{} 전체:{}ms 건당:{}ms", events.size(), time, time / events.size());
	}
}
