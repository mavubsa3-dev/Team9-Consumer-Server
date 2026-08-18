package com.example.consumerserver.domain.delivery.listener;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.delivery.service.DeliveryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryListener {

	private final DeliveryService deliveryService;

	@KafkaListener(
		topics = "payment-completed",
		groupId = "payment-completed-delivery",
		containerFactory = "paymentCompletedDeliveryEventKafkaListenerContainerFactory",
		batch = "true"
	) public void saveDelivery(List<PaymentCompletedEvent> events){

		long start = System.currentTimeMillis();
		int size = events.size();

		for (PaymentCompletedEvent event : events) {
			deliveryService.saveDeliveryInfo(event);
		}

		long totalTime = System.currentTimeMillis() - start;
		log.info("[배송 배치 처리] 건수:{} 전체:{}ms 건당:{}ms", size, totalTime, totalTime / size);
	}
}
