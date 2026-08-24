package com.example.consumerserver.common.config.kafka.event;

import java.time.LocalDateTime;
import java.util.List;

public record PaymentCompletedEvent(
	Long paymentId,
	Long userId,
	Long orderId,
	String orderNumber,
	Long totalAmount,
	String address,
	List<OrderItemInfo> orderItems,
	LocalDateTime completedAt
) {
}
