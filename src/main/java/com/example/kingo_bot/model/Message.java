package com.example.kingo_bot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.ProtocolFamily;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty(message = "내용은 필수항목입니다.")
    String content;

    long timestamp;

    @ManyToOne
    Room room_at;

    @ManyToOne
    SiteUser writer;
}
