package com.example.consumerserver.common.config.kafka.event;

public record OrderItemInfo(
	Long productId,
	String productName,
	Integer quantity
) {
}
