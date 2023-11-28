package com.example.kingo_bot.request;

import com.example.kingo_bot.model.Message;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MessageRequest {
    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;

    @JsonCreator
    public MessageRequest(@JsonProperty("content") String content) {
        this.content = content;
    }
}
