package com.womensecurity.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.womensecurity.backend.entity.ChatMessage;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByUserId(Long userId);

}