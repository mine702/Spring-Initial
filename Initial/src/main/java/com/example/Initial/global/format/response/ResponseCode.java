package com.example.Initial.global.format.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    MEMBER_SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입이 정상적으로 완료되었습니다."),
    GET_LIST_SUCCESS(HttpStatus.OK, "리스트 조회 성공"),
    GET_JSON_SUCCESS(HttpStatus.OK, "JSON 조회 성공");

    private final HttpStatus status;
    private final String message;
}
