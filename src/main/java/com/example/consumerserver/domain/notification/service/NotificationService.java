package com.example.consumerserver.domain.notification.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;
import com.example.consumerserver.domain.notification.dto.SendEmailMessageRequest;
import com.example.consumerserver.domain.notification.entity.Notifications;
import com.example.consumerserver.domain.notification.repository.NotificataionRepository;
import com.example.consumerserver.domain.user.entity.User;
import com.example.consumerserver.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificataionRepository notificataionRepository;
	private final UserRepository userRepository;
	private final EmailService emailService;

	@Transactional
	public void saveNotifications(List<PaymentCompletedEvent> events) {
		List<Notifications> entities = events.stream()
			.map(e -> new Notifications(e.paymentId(), e.userId(), title(e), dbMessage(e)))
			.toList();

		try {
			notificataionRepository.saveAll(entities);
		} catch (DataIntegrityViolationException e) {
			log.warn("알림 중복 저장 방지됨 (DB 제약)");
		}
	}

	public void sendEmails(List<PaymentCompletedEvent> events) {
		List<Long> userIds = events.stream()
			.map(PaymentCompletedEvent::userId)
			.distinct()
			.toList();

		Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
			.collect(Collectors.toMap(User::getId, Function.identity()));

		List<SendEmailMessageRequest> requests = events.stream()
			.map(e -> {
				User user = userMap.get(e.userId());
				if (user == null) {
					log.warn("사용자 없음, 스킵: userId={}", e.userId());
					return null;
				}
				return new SendEmailMessageRequest(
					user.getEmail(), title(e), htmlMessage(e, user.getName()));
			})
			.filter(Objects::nonNull)
			.toList();

		emailService.sendAll(requests);
	}

	private String title(PaymentCompletedEvent e) {
		return "[결제 완료] 주문 번호 : " + e.orderNumber();
	}

	private String dbMessage(PaymentCompletedEvent e) {
		String products = e.orderItems().stream()
			.map(i -> String.format("- %s %d개", i.productName(), i.quantity()))
			.collect(Collectors.joining("\n"));
		return String.format(
			"주문 ID: %s\n주문번호: %s\n주문 상품:\n%s\n결제 금액: %d원\n결제 일시: %s",
			e.orderId(), e.orderNumber(), products, e.totalAmount(), e.completedAt());
	}

	private String htmlMessage(PaymentCompletedEvent e, String userName) {
		String products = e.orderItems().stream()
			.map(i -> String.format("<p>- %s x %d개</p>", i.productName(), i.quantity()))
			.collect(Collectors.joining());
		return String.format(
			"<h3>결제가 정상적으로 완료되었습니다.</h3>"
				+ "<p><b>주문ID:</b> %s</p><p><b>주문번호:</b> %s</p>"
				+ "<p><b>이름:</b> %s</p><p><b>주문 상품:</b></p>%s"
				+ "<p><b>결제금액:</b> %d원</p><p><b>결제일시:</b> %s</p>",
			e.orderId(), e.orderNumber(), userName, products, e.totalAmount(), e.completedAt());
	}

	public List<PaymentCompletedEvent> filterNewEvents(List<PaymentCompletedEvent> events) {
		List<Long> paymentIds = events.stream()
			.map(PaymentCompletedEvent::paymentId)
			.toList();

		Set<Long> alreadyProcessed = notificataionRepository.findExistingPaymentIds(paymentIds);

		List<PaymentCompletedEvent> newEvents = events.stream()
			.filter(e -> !alreadyProcessed.contains(e.paymentId()))
			.toList();

		if (newEvents.size() < events.size()) {
			log.info("중복 이벤트 {}건 제외됨", events.size() - newEvents.size());
		}

		return newEvents;
	}
}
