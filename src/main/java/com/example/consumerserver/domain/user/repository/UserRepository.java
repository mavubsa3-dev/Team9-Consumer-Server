package com.example.consumerserver.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.consumerserver.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
