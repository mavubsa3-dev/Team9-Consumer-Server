package com.example.consumerserver.domain.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.consumerserver.domain.point.entity.Point;

public interface PointRepository extends JpaRepository<Point, Long> {
}
