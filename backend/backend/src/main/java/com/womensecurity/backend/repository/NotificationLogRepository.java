package com.womensecurity.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.NotificationLog;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

}