package com.example.kingo_bot.request;

import com.example.kingo_bot.model.SiteUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRequest {
    @Size(min = 3, max = 25)
    @NotEmpty(message = "사용자ID는 필수항목입니다.")
    private String username;

    @Size(min = 1, max = 25)
    @NotEmpty(message = "닉네임은 필수항목입니다.")
    private String nickname;

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String password2;

    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email
    private String email;

    public SiteUser toEntity() {
        return SiteUser.builder()
                .username(username)
                .nickname(nickname)
                .password(password1)
                .email(email)
                .build();
    }
}
