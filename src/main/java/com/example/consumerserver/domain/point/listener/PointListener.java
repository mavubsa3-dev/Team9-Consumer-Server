package com.example.consumerserver.domain.point.listener;

import java.util.List;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointListener {

	private final PointService pointService;

	@KafkaListener(
		topics = "payment-completed",
		groupId = "payment-completed-point",
		containerFactory = "paymentCompletedPointEventKafkaListenerContainerFactory",
		batch = "true"
	) public void consumePoint(List<PaymentCompletedEvent> events){

		for(PaymentCompletedEvent event : events) {
			pointService.savedPoint(event);
		}

	}
}
