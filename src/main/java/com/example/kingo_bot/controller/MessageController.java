package com.example.kingo_bot.controller;

import com.example.kingo_bot.responce.MessageResponse;
import com.example.kingo_bot.request.MessageRequest;
import com.example.kingo_bot.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public MessageResponse greeting(Principal principal, MessageRequest messageRequest) throws Exception {
        return messageService.saveMessage(principal, messageRequest);
    }

    @MessageMapping("/connect")
    @SendTo("/topic/greetings")
    public List<MessageResponse> initialConnection(@Payload Long lastReceivedTimestamp) {
        return messageService.getMessagesAfterTimestamp(lastReceivedTimestamp);
    }

    @PostMapping("/api/message")
    public ResponseEntity<?> sendMessage(Principal principal, @RequestBody MessageRequest messageRequest) {
        if (messageRequest == null || messageRequest.getContent().isEmpty()) {
            return ResponseEntity.badRequest().body("내용은 필수항목입니다.");
        } else if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        MessageResponse messageResponse = messageService.saveMessage(principal, messageRequest);
        try {
            messagingTemplate.convertAndSend("/topic/greetings", messageResponse);
        } catch (MessageDeliveryException e) {
            // Log the exception and return a meaningful response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Message delivery failed.");
        }        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
    }

    @GetMapping("/api/messages/previous")
    public ResponseEntity<List<MessageResponse>> getPreviousMessages(@RequestParam Long lastReceivedTimestamp) {
        List<MessageResponse> messages = messageService.getMessagesBeforeTimestamp(lastReceivedTimestamp, 100);
        return ResponseEntity.ok(messages);
    }
}
