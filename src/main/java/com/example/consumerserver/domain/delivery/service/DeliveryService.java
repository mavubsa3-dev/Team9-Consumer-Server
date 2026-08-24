package com.example.consumerserver.domain.delivery.service;

import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.delivery.entity.Delivery;
import com.example.consumerserver.domain.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

	private final DeliveryRepository deliveryRepository;


	@Transactional
	public void saveDeliveryInfo(PaymentCompletedEvent event){

		if (deliveryRepository.existsByOrderId(event.orderId())){
			log.info("이미 처리된 배송 정보입니다. orderId : {} ", event.orderId());
			return;
		}

		String products = event.orderItems().stream()
			.map(item -> item.productName() + " " + item.quantity() + "개")
			.collect(Collectors.joining(", "));
		try {
			Delivery delivery = new Delivery(
				event.orderId(),
				event.userId(),
				event.address(),
				products
			);

			deliveryRepository.save(delivery);
		} catch (DataIntegrityViolationException e) {
			log.warn("데이터 중복 저장 방지. orderId : {} ", event.orderId());
		}
	}
}
