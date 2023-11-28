package com.example.kingo_bot.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MessageResponse {
    private String name;
    private String content;
    private long timestamp;
}
