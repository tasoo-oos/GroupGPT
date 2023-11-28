package com.example.kingo_bot.service;

import com.example.kingo_bot.model.Room;
import com.example.kingo_bot.repository.RoomRepository;

public class RoomService {
    RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public void save(Room chatRoom) {
        roomRepository.save(chatRoom);
    }
}
