package com.example.consumerserver.domain.ranking.listener;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.ranking.service.RankingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RankingListener {

	private final RankingService rankingService;

	@KafkaListener(
		topics = "payment-completed",
		groupId = "payment-completed-ranking",
		containerFactory = "paymentCompletedRankingEventKafkaListenerContainerFactory",
		batch = "true"
	) public void consumeRanking(List<PaymentCompletedEvent> events) {

		for (PaymentCompletedEvent event : events) {
			rankingService.increaseScore(event);
		}

	}
}
