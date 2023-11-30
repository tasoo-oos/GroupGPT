package com.example.kingo_bot.repository;

import com.example.kingo_bot.model.Message;
import com.example.kingo_bot.responce.MessageResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long>{

    Optional<Message> findById(Long Id);
    List<Message> findAll();
    List<Message> findTop10ByTimestampBeforeOrderByTimestampDesc(Long lastReceivedTimestamp);
}
