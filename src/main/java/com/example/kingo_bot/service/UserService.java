package com.example.kingo_bot.service;


import com.example.kingo_bot.model.SiteUser;
import com.example.kingo_bot.repository.UserRepository;
import com.example.kingo_bot.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(UserRequest userRequest) {
        SiteUser user = new SiteUser();
        user.setUsername(userRequest.getUsername());
        user.setNickname(userRequest.getNickname());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword1()));
        user.setEmail(userRequest.getEmail());
        return userRepository.save(user);
    }
}
