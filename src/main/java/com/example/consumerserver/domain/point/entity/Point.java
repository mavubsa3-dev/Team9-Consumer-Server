package com.example.consumerserver.domain.point.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table( name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_id", nullable = false)
	private Long orderId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "totalAmount", nullable = false)
	private Long totalAmount;

	@Column(name = "points", nullable = false)
	private Long points;

	public Point(Long orderId, Long userId, Long totalAmount, Long points){

		this.orderId = orderId;
		this.userId = userId;
		this.totalAmount = totalAmount;
		this.points = points;
	}

}
