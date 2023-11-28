package com.example.kingo_bot.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@Getter
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private long id; // Unique identifier for the chat room

    @Column(length = 64)
    private String name; // Name of the chat room

    @OneToMany(mappedBy = "room_at", cascade = CascadeType.REMOVE)
    private ConcurrentHashMap<Long, Message> messages;

    @ManyToOne
    private SiteUser owner;

    public Room(String name) {
        this.name = name;
    }
}
