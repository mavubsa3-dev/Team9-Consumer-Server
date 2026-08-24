package com.example.consumerserver.domain.notification.listener;

import java.util.List;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class NotificationListener {

	private final NotificationService notificationService;

	@KafkaListener(
		topics = "payment-completed",
		groupId = "payment-completed-notification",
		containerFactory = "paymentCompletedNotificationEventKafkaListenerContainerFactory",
		batch = "true"
	) public void consumeNotification(List<PaymentCompletedEvent> events) {

		List<PaymentCompletedEvent> newEvents = notificationService.filterNewEvents(events);

		if (newEvents.isEmpty()) {
			return;
		}

		notificationService.saveNotifications(newEvents);
		notificationService.sendEmails(newEvents);

	}
}
