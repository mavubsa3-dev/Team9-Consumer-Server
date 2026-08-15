package com.example.consumerserver.domain.notification.entity;

import com.example.consumerserver.common.config.basetime.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notifications extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	private String title;

	private String message;

	@Column(name = "is_read", nullable = false)
	private boolean isRead;

	public Notifications(Long userId, String title, String message){
		this.userId = userId;
		this.title = title;
		this.message = message;
		this.isRead = false;
	}
}
