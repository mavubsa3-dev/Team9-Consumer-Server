package com.example.consumerserver.common.config.kafka.listener;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.delivery.service.DeliveryService;
import com.example.consumerserver.domain.notification.service.NotificationService;
import com.example.consumerserver.domain.point.service.PointService;
import com.example.consumerserver.domain.ranking.service.RankingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommonListener {

	private final NotificationService notificationService;
	private final DeliveryService deliveryService;
	private final RankingService rankingService;
	private final PointService pointService;

	@KafkaListener(
		topics = "payment-completed",
		groupId = "payment-completed-notification",
		containerFactory = "paymentCompletedNotificationEventKafkaListenerContainerFactory",
		batch = "true"
	) public void consume(List<PaymentCompletedEvent> events) {

		long totalStart = System.currentTimeMillis();
		int size = events.size();

		log.info("[배치 수신] 건수:{}", size);

		long notiStart = System.currentTimeMillis();
		notificationService.saveNotifications(events);
		notificationService.sendEmails(events);
		long notiTime = System.currentTimeMillis() - notiStart;

		long etcStart = System.currentTimeMillis();
		for (PaymentCompletedEvent event : events) {
			pointService.savedPoint(event);
			deliveryService.saveDeliveryInfo(event);
			rankingService.increaseScore(event);
		}
		long etcTime = System.currentTimeMillis() - etcStart;

		long totalTime = System.currentTimeMillis() - totalStart;

		log.info("[배치 처리시간] 건수:{} 전체:{}ms 알림:{}ms 나머지:{}ms 건당:{}ms",
			size, totalTime, notiTime, etcTime, totalTime / size);
	}
}
