package com.example.kingo_bot.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchConnectionDetails;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String nickname;

    private String password;

    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<Room> room;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<Message> message;
}
