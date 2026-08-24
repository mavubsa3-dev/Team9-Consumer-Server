package com.example.consumerserver.domain.notification.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.consumerserver.domain.notification.entity.Notifications;

public interface NotificataionRepository extends JpaRepository<Notifications, Long> {

	@Query("SELECT n.paymentId FROM Notifications n WHERE n.paymentId IN :paymentIds")
	Set<Long> findExistingPaymentIds(@Param("paymentIds") List<Long> paymentIds);
}
