package com.example.consumerserver.domain.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.consumerserver.domain.delivery.entity.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
