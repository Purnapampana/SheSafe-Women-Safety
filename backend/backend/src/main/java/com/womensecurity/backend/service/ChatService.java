package com.womensecurity.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.womensecurity.backend.dto.ChatRequest;
import com.womensecurity.backend.entity.ChatMessage;
import com.womensecurity.backend.repository.ChatRepository;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    // CREATE
    public String sendMessage(ChatRequest request) {

        ChatMessage chat = new ChatMessage();

        chat.setUserId(request.getUserId());
        chat.setUserMessage(request.getUserMessage());

        String message = request.getUserMessage().toLowerCase();

        String response;

        if (message.contains("hello")) {
            response = "Hello! How can I help you today?";
        }
        else if (message.contains("sos")) {
            response = "Please press the SOS button immediately.";
        }
        else if (message.contains("unsafe")) {
            response = "Move to a safe public place and alert your emergency contacts.";
        }
        else if (message.contains("police")) {
            response = "Please check the nearest police station using the GPS module.";
        }
        else if (message.contains("help")) {
            response = "I'm here to help. Tell me your emergency.";
        }
        else {
            response = "Sorry, I didn't understand. Please try another question.";
        }

        chat.setBotResponse(response);
        chat.setCreatedAt(LocalDateTime.now());

        chatRepository.save(chat);

        return response;
    }

    // READ USER CHAT
    public List<ChatMessage> getChatHistory(Long userId) {

        return chatRepository.findByUserId(userId);

    }

    // READ ALL
    public List<ChatMessage> getAllChats() {

        return chatRepository.findAll();

    }

    // UPDATE
    public ChatMessage updateChat(Long id, ChatMessage updatedChat) {

        Optional<ChatMessage> chat = chatRepository.findById(id);

        if (chat.isPresent()) {

            ChatMessage existing = chat.get();

            existing.setUserMessage(updatedChat.getUserMessage());
            existing.setBotResponse(updatedChat.getBotResponse());

            return chatRepository.save(existing);
        }

        return null;
    }

    // DELETE
    public String deleteChat(Long id) {

        if (chatRepository.existsById(id)) {

            chatRepository.deleteById(id);

            return "Chat Deleted Successfully";
        }

        return "Chat Not Found";
    }

}