package com.example.Initial.global.handler;

import com.example.Initial.global.exception.user.PassWordIncorrectException;
import com.example.Initial.global.exception.user.UserNotFoundException;
import com.example.Initial.global.format.code.ApiResponse;
import com.example.Initial.global.format.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ApiResponse response;

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handle(Exception e) {
        log.error("Exception", e);
        return response.error(ErrorCode.GLOBAL_UNEXPECTED_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<?> handle(UserNotFoundException e) {
        log.error("UserNotFoundException = {}", e.getMessage());
        return response.error(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(PassWordIncorrectException.class)
    protected ResponseEntity<?> handle(PassWordIncorrectException e) {
        log.error("PassWordIncorrectException = {}", e.getMessage());
        return response.error(e.getErrorCode(), e.getMessage());
    }
    /**
     * 유효성 검사 실패시 발생하는 예외
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<ApiResponse.FieldError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ApiResponse.FieldError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatusCode.valueOf(status.value()))
                .body(response.fail(ErrorCode.VALIDATION_ERROR, errors));
    }
}
