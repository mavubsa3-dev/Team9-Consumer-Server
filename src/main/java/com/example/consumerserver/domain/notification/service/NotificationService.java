package com.example.consumerserver.domain.notification.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.consumerserver.common.config.kafka.event.OrderItemInfo;
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
	public void saveAndSend(PaymentCompletedEvent event){

		String title = "[결제 완료] 주문 번호 : " + event.orderNumber();

		saveNotification(title, event);
		sendEmailNotification(event, title);
	}

	private void saveNotification(String title, PaymentCompletedEvent event){

		String dbProductList = formatOrderItems(event.orderItems(), "- ", "%s %d개");

		String dbMessage = String.format(
			"주문 ID: %s\n" +
			"주문번호: %s\n" +
				"주문 상품:\n%s\n" +
				"결제 금액: %d원\n" +
				"결제 일시: %s",
			event.orderId(),
			event.orderNumber(),
			dbProductList,
			event.totalAmount(),
			event.completedAt()
		);

		notificataionRepository.save(new Notifications(event.userId(), title, dbMessage));
	}

	private void sendEmailNotification(PaymentCompletedEvent event, String title){

		User user = userRepository.findById(event.userId()).orElseThrow(
			() -> new RuntimeException("사용자를 찾을 수 없습니다.")
		);

		String htmlProductList = event.orderItems().stream()
			.map(item -> String.format("<p>- %s x %d개</p>", item.productName(), item.quantity()))
			.collect(Collectors.joining(""));

		String message = String.format(
			"<h3>결제가 정상적으로 완료되었습니다.</h3>" +
				"<p><b>주문ID:</b> %s</p>" +
				"<p><b>주문번호:</b> %s</p>" +
				"<p><b>이름:</b> %s</p>" +
				"<p><b>주문 상품:</b></p>" +
				"%s" +
				"<p><b>결제금액:</b> %d원</p>" +
				"<p><b>결제일시:</b> %s</p>",
			event.orderId(),
			event.orderNumber(),
			user.getName(),
			htmlProductList,
			event.totalAmount(),
			event.completedAt()
		);


		emailService.send(new SendEmailMessageRequest(user.getEmail(), title, message));
	}

	private String formatOrderItems(List<OrderItemInfo> orderItems, String prefix, String format){
		return orderItems.stream()
			.map(item -> String.format(format, item.productName(), item.quantity()))
			.collect(Collectors.joining("/n" + prefix, prefix, ""));
	}
}
