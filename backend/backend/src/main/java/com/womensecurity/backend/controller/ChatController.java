package com.womensecurity.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.womensecurity.backend.dto.ChatRequest;
import com.womensecurity.backend.entity.ChatMessage;
import com.womensecurity.backend.service.ChatService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin("*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // CREATE
    @PostMapping("/send")
    public String sendMessage(@RequestBody ChatRequest request) {

        return chatService.sendMessage(request);

    }

    // READ USER CHAT
    @GetMapping("/user/{userId}")
    public List<ChatMessage> getChatHistory(@PathVariable Long userId) {

        return chatService.getChatHistory(userId);

    }

    // READ ALL
    @GetMapping("/all")
    public List<ChatMessage> getAllChats() {

        return chatService.getAllChats();

    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ChatMessage updateChat(
            @PathVariable Long id,
            @RequestBody ChatMessage chat) {

        return chatService.updateChat(id, chat);

    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public String deleteChat(@PathVariable Long id) {

        return chatService.deleteChat(id);

    }

}