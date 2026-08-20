package com.example.consumerserver.domain.point.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.point.entity.Point;
import com.example.consumerserver.domain.point.repository.PointRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {

	private final PointRepository pointRepository;

	@Transactional
	public void savedPoint(PaymentCompletedEvent event){

		if (pointRepository.existsByOrderId(event.orderId())) {
			log.info("이미 적립금이 처리된 주문입니다. orderId: {}", event.orderId());
			return;
		}

		Long point = Math.round(event.totalAmount() * 3 / 100.0);

		Point savingPoint = new Point(
			event.orderId(),
			event.userId(),
			event.totalAmount(),
			point
		);

		try {
			pointRepository.save(savingPoint);
		} catch (DataIntegrityViolationException e) {
			log.warn("적립금 중복 저장 방지됨 (DB 제약). orderId: {}", event.orderId());
		}
	}
}
