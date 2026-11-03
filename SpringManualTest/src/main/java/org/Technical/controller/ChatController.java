package org.Technical.controller;

import jakarta.validation.Valid;
import org.Technical.dto.ChatRequest;
import org.Technical.dto.ChatResponse;
import org.Technical.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {

        String answer = chatService.chat(request.getMessage());

        return new ChatResponse(answer);
    }
}
