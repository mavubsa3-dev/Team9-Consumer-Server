package com.example.consumerserver.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.consumerserver.domain.notification.entity.Notifications;

public interface NotificataionRepository extends JpaRepository<Notifications, Long> {
}
