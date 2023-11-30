package com.example.kingo_bot.service;

import com.example.kingo_bot.model.Message;
import com.example.kingo_bot.model.SiteUser;
import com.example.kingo_bot.repository.MessageRepository;
import com.example.kingo_bot.repository.UserRepository;
import com.example.kingo_bot.responce.MessageResponse;
import com.example.kingo_bot.request.MessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.nio.file.AccessDeniedException;
import java.rmi.ServerException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageResponse saveMessage(Principal principal, MessageRequest messageRequest) throws Exception{
        Optional<SiteUser> optionalUser = userRepository.findByUsername(principal.getName());
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("사용자가 존재하지 않습니다. 로그인을 하셨나요?");
        }
        try{
            Message saved = messageRepository.save(Message.builder()
                    .content(messageRequest.getContent())
                    .timestamp(System.currentTimeMillis())
                    .writer(optionalUser.get())
                    .build());
            return MessageResponse.builder()
                    .id(saved.getId())
                    .content(saved.getContent())
                    .name(saved.getWriter().getNickname())
                    .timestamp(saved.getTimestamp())
                    .build();
        } catch (Exception e) {
            throw new ServerException("서버 오류");
        }
    }

    public List<MessageResponse> get10MessagesBeforeTimestamp(Long lastReceivedTimestamp) throws Exception{
    // Fetch messages from the database that were sent before the given timestamp
    // Limit the number of messages to the given limit
    // This is just a placeholder, replace with your actual database query
        try{
            List<Message> desc = messageRepository.findTop10ByTimestampBeforeOrderByTimestampDesc(lastReceivedTimestamp);
            return desc.stream()
                    .map(message -> MessageResponse.builder()
                            .id(message.getId())
                            .content(message.getContent())
                            .name(message.getWriter().getNickname())
                            .timestamp(message.getTimestamp())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServerException("서버 오류");
        }
    }

    public MessageResponse editMessage(Long id, MessageRequest messageRequest, Principal principal) throws Exception{
        if(messageRequest == null || messageRequest.getContent().isEmpty()) {
            throw new AccessDeniedException("내용은 필수항목입니다.");
        } else if(principal == null) {
            throw new UsernameNotFoundException("로그인이 필요합니다.");
        }
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isEmpty()) {
            // Handle the case where the user is not found
            // This could be throwing an exception, returning a default value, etc.
            throw new UsernameNotFoundException("메세지 수신 실패");
        } else if(!optionalMessage.get().getWriter().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        try{
            Message message = optionalMessage.get();
            message.setContent(messageRequest.getContent());
            Message saved = messageRepository.save(message);
            return MessageResponse.builder()
                    .id(saved.getId())
                    .content(saved.getContent())
                    .name(saved.getWriter().getNickname())
                    .timestamp(saved.getTimestamp())
                    .build();
        } catch (Exception e) {
            throw new ServerException("서버 오류");
        }
    }
}
