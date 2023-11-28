package com.example.kingo_bot.repository;

import com.example.kingo_bot.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
