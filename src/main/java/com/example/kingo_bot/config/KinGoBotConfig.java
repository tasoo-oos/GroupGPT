package com.example.kingo_bot.config;

import com.example.kingo_bot.repository.MessageRepository;
import com.example.kingo_bot.repository.RoomRepository;
import com.example.kingo_bot.repository.UserRepository;
import com.example.kingo_bot.service.MessageService;
import com.example.kingo_bot.service.RoomService;
import com.example.kingo_bot.service.UserSecurityService;
import com.example.kingo_bot.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class KinGoBotConfig {

    RoomRepository roomRepository;
    MessageRepository messageRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;


    public KinGoBotConfig(RoomRepository roomRepository,
                          MessageRepository messageRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public RoomService getRoomService() {
        return new RoomService(roomRepository);
    }

    @Bean
    public MessageService getMessageService() {
        return new MessageService(messageRepository, userRepository);
    }

    @Bean
    public UserService getUserService() {
        return new UserService(userRepository, passwordEncoder);
    }

    @Bean
    public UserSecurityService getUserSecurityService() {
        return new UserSecurityService(userRepository);
    }
}
