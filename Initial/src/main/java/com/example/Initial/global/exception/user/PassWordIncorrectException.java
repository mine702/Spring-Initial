package com.example.Initial.global.exception.user;

import com.example.Initial.global.format.response.ErrorCode;
import lombok.Getter;

@Getter
public class PassWordIncorrectException extends RuntimeException {
    private final ErrorCode errorCode;

    public PassWordIncorrectException() {
        super(ErrorCode.PASS_WORD_INCORRECT.getMessage());
        this.errorCode = ErrorCode.PASS_WORD_INCORRECT;
    }
}
