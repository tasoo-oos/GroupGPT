package com.example.kingo_bot.service;

import com.example.kingo_bot.model.Message;
import com.example.kingo_bot.model.SiteUser;
import com.example.kingo_bot.repository.MessageRepository;
import com.example.kingo_bot.repository.UserRepository;
import com.example.kingo_bot.responce.MessageResponse;
import com.example.kingo_bot.request.MessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public MessageResponse saveMessage(Principal principal, MessageRequest messageRequest) {
        Optional<SiteUser> optionalUser = userRepository.findByUsername(principal.getName());
        if (optionalUser.isEmpty()) {
            // Handle the case where the user is not found
            // This could be throwing an exception, returning a default value, etc.
            throw new UsernameNotFoundException("User not found");
        }
        SiteUser user = optionalUser.get();
        Message saved = messageRepository.save(Message.builder()
                .content(messageRequest.getContent())
                .timestamp(System.currentTimeMillis())
                .writer(user)
                .build());
        return MessageResponse.builder()
                .content(saved.getContent())
                .name(user.getNickname())
                .timestamp(saved.getTimestamp())
                .build();
    }

public List<MessageResponse> getMessagesAfterTimestamp(Long timestamp) {
    List<Message> messages = messageRepository.findMessagesAfterTimestamp(timestamp);
    return messages.stream()
            .map(message -> {
                SiteUser writer = message.getWriter();
                String nickname = (writer != null) ? writer.getNickname() : "Unknown";
                return MessageResponse.builder()
                        .content(message.getContent())
                        .name(nickname)
                        .timestamp(message.getTimestamp())
                        .build();
            })
            .collect(Collectors.toList());
}

    public List<MessageResponse> getMessagesBeforeTimestamp(Long lastReceivedTimestamp, int limit) {
    // Fetch messages from the database that were sent before the given timestamp
    // Limit the number of messages to the given limit
    // This is just a placeholder, replace with your actual database query
        List<Message> desc = messageRepository.findTop100ByTimestampBeforeOrderByTimestampDesc(lastReceivedTimestamp);
        return desc.stream()
                .map(message -> MessageResponse.builder()
                        .content(message.getContent())
                        .name(message.getWriter().getNickname())
                        .timestamp(message.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }
}
