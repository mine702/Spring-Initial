package com.example.Initial.auth.module;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuthReq {
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,16}$", message = "비밀번호는 영문, 숫자를 포함한 8~16자리여야 합니다.")
    private String password;
}
