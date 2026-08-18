package com.example.consumerserver.domain.delivery.service;

import org.springframework.stereotype.Service;
import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.delivery.repository.DeliveryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

	private final DeliveryRepository deliveryRepository;
	private final SaveDeliveryService saveDeliveryService;

	public void saveDeliveryInfo(PaymentCompletedEvent event){

		if (deliveryRepository.existsByOrderId(event.orderId())){
			log.info("이미 처리된 배송 정보입니다. orderId : {} ", event.orderId());
			return;
		}

		try{

			// 배송 요청 API 호출 시뮬레이션 위한 Delay 가정
			Thread.sleep(300);

		} catch (InterruptedException e){

			Thread.currentThread().interrupt();
			throw new RuntimeException("배송 요청 시뮬레이션 중단", e);
		}

		saveDeliveryService.saveToDb(event);
	}
}
