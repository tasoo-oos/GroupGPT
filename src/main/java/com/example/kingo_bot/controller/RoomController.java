package com.example.kingo_bot.controller;

import com.example.kingo_bot.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class RoomController {
    private final RoomService roomService;

    @GetMapping("/list")
    public String chatList(Model model){
        return "chat_list";
    }

    @GetMapping("/room")
    public String chatRoom(Model model){
        return "chat_room";
    }

}
