package com.example.flowerplatform.security.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Setter
@Getter
@ToString
public class LoginRequestDto {

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일을 필수 항목입니다.")
    private final String email;

    @NotBlank(message =  "비밀번호는 필수 항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자리에서 20자 사이입니다.")
    private final String password;
}