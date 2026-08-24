package com.example.consumerserver.domain.delivery.listener;

import java.util.List;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class DeliveryListener {

	private final DeliveryService deliveryService;

	@KafkaListener(
		topics = "payment-completed",
		groupId = "payment-completed-delivery",
		containerFactory = "paymentCompletedDeliveryEventKafkaListenerContainerFactory",
		batch = "true"
	) public void saveDelivery(List<PaymentCompletedEvent> events){


		for (PaymentCompletedEvent event : events) {
			deliveryService.saveDeliveryInfo(event);
		}

	}
}
