package com.example.kingo_bot.controller;

import com.example.kingo_bot.responce.MessageResponse;
import com.example.kingo_bot.request.MessageRequest;
import com.example.kingo_bot.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/api/is_logged_in")
    public ResponseEntity<?> sendMessage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            // User is not authenticated
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // Rest of the sendMessage code...
        return ResponseEntity.ok("로그인이 되어있습니다.");
    }

    @PostMapping("/api/message")
    public ResponseEntity<?> sendMessage(Principal principal, @RequestBody MessageRequest messageRequest) {
        if (messageRequest == null || messageRequest.getContent().isEmpty()) {
            return ResponseEntity.badRequest().body("내용은 필수항목입니다.");
        } else if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try{
            MessageResponse messageResponse = messageService.saveMessage(principal, messageRequest);
            messagingTemplate.convertAndSend("/topic/messages", messageResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/api/messages/previous")
    public ResponseEntity<?> getPreviousMessages(@RequestParam Long lastReceivedTimestamp) {
        try {
            List<MessageResponse> messages = messageService.get10MessagesBeforeTimestamp(lastReceivedTimestamp);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/api/message/edit/{id}")
    public ResponseEntity<?> editMessage(@PathVariable Long id, @RequestBody MessageRequest messageRequest, Principal principal) {
        if (messageRequest == null || messageRequest.getContent().isEmpty()) {
            return ResponseEntity.badRequest().body("내용은 필수 항목입니다.");
        }
        try {
            MessageResponse messageResponse = messageService.editMessage(id, messageRequest, principal);
            return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

//https://github.com/HamaWhiteGG/langchain-java
