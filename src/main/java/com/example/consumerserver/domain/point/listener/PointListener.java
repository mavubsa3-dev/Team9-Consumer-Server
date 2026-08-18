package com.example.consumerserver.domain.point.listener;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.point.service.PointService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PointListener {

	private final PointService pointService;

	@KafkaListener(
		topics = "payment-completed",
		groupId = "payment-completed-point",
		containerFactory = "paymentCompletedPointEventKafkaListenerContainerFactory",
		batch = "true"
	) public void consumePoint(List<PaymentCompletedEvent> events){

		long start = System.currentTimeMillis();

		for(PaymentCompletedEvent event : events) {
			pointService.savedPoint(event);
		}

		long time = System.currentTimeMillis() - start;

		log.info("[포인트 배치 처리] 건수:{} 전체:{}ms 건당:{}ms", events.size(), time, time / events.size());
	}
}
