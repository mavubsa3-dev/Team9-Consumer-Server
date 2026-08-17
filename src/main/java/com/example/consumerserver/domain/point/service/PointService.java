package com.example.consumerserver.domain.point.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.point.entity.Point;
import com.example.consumerserver.domain.point.repository.PointRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {

	private final PointRepository pointRepository;

	@Transactional
	public void savedPoint(PaymentCompletedEvent event){

		Long point = Math.round(event.totalAmount() * 3 / 100.0);

		Point savingPoint = new Point(
			event.orderId(),
			event.userId(),
			event.totalAmount(),
			point
		);

		pointRepository.save(savingPoint);
	}
}
