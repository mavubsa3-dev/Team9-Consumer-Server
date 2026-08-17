package com.example.consumerserver.domain.delivery.service;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.delivery.entity.Delivery;
import com.example.consumerserver.domain.delivery.repository.DeliveryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {

	private final DeliveryRepository deliveryRepository;

	@Transactional
	public void saveDeliveryInfo(PaymentCompletedEvent event){

		String products = event.orderItems().stream()
			.map(item -> item.productName() + " " + item.quantity() + "개")
			.collect(Collectors.joining(", "));

		Delivery delivery = new Delivery(
			event.orderId(),
			event.userId(),
			event.address(),
			products
		);

		deliveryRepository.save(delivery);
	}
}
