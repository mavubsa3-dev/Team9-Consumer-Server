package com.example.consumerserver.domain.ranking.service;

import java.time.Duration;
import java.time.LocalDate;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class RankingService {

	private final StringRedisTemplate stringRedisTemplate;
	private static final String PRODUCT_RANKING_KEY = "product:ranking:";
	private static final String RANKING_IDS_KEY = "ranking:products:ids:";

	public void increaseScore(PaymentCompletedEvent event){

		LocalDate currentDate = LocalDate.now();

		String key = PRODUCT_RANKING_KEY + currentDate;
		String idsKey = RANKING_IDS_KEY + currentDate;

		event.orderItems().forEach(item -> {

			String productInfo = item.productId() + ":" + item.productName();
			int quantity = item.quantity();

			stringRedisTemplate.opsForZSet().incrementScore(key, productInfo, quantity);

			stringRedisTemplate.opsForSet().add(idsKey, item.productId().toString());
		});

		stringRedisTemplate.expire(idsKey, Duration.ofDays(8));
		stringRedisTemplate.expire(key, Duration.ofDays(8));
	}
}
