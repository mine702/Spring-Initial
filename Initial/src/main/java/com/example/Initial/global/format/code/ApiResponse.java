package com.example.Initial.global.format.code;

import com.example.Initial.global.format.response.ErrorCode;
import com.example.Initial.global.format.response.ResponseCode;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ApiResponse {

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAIL = "FAILED";
    private static final String STATUS_ERROR = "ERROR";

    private <T, E> ResponseEntity<?> get(
            String status,
            @Nullable String message,
            @Nullable T data,
            @Nullable E errors,
            HttpStatus httpStatus
    ) {
        if (status.equals(STATUS_SUCCESS)) {

            return new ResponseEntity<>(SucceededBody.builder()
                    .status(status)
                    .message(message)
                    .data(data)
                    .build(),
                    httpStatus);

        } else if (status.equals(STATUS_FAIL)) {
            return new ResponseEntity<>(FailedBody.builder()
                    .status(status)
                    .message(message)
                    .errors(errors)
                    .build(),
                    httpStatus);
        } else if (status.equals(STATUS_ERROR)) {
            return new ResponseEntity<>(ErroredBody.builder()
                    .status(status)
                    .message(message)
                    .build(),
                    httpStatus);
        } else {
            throw new RuntimeException("Api Response Error");
        }
    }

    private <T> ResponseEntity<?> get(
            String status,
            @Nullable String message,
            @Nullable List<T> items,
            @Nullable Long page,
            @Nullable Integer size,
            @Nullable Long total,
            @Nullable Integer totalPages,
            @Nullable Boolean first,
            @Nullable Boolean last,
            HttpStatus httpStatus
    ) {
        if (status.equals(STATUS_SUCCESS)) {
            PagedBody.PagedContent<T> content = PagedBody.PagedContent.<T>builder()
                    .items(items != null ? items : Collections.emptyList())
                    .page(page)
                    .size(size)
                    .total(total)
                    .totalPages(totalPages)
                    .first(first)
                    .last(last)
                    .build();

            return new ResponseEntity<>(PagedBody.<T>builder()
                    .status(status)
                    .message(message)
                    .data(content)
                    .build(), httpStatus);
        }
        throw new RuntimeException("Invalid status for paged response");
    }

    private <T> ResponseEntity<?> get(
            String status,
            @Nullable String message,
            @Nullable List<T> items,
            @Nullable Long page,
            @Nullable Integer size,
            @Nullable Boolean hasNext,
            @Nullable Boolean hasPrevious,
            HttpStatus httpStatus
    ) {
        if (status.equals(STATUS_SUCCESS)) {
            SlicedBody.SlicedContent<T> content = SlicedBody.SlicedContent.<T>builder()
                    .items(items != null ? items : Collections.emptyList())
                    .page(page)
                    .size(size)
                    .hasNext(hasNext != null ? hasNext : false)
                    .hasPrevious(hasPrevious != null ? hasPrevious : false)
                    .build();

            return new ResponseEntity<>(SlicedBody.<T>builder()
                    .status(status)
                    .message(message)
                    .data(content)
                    .build(), httpStatus);
        }
        throw new RuntimeException("Invalid status for sliced response");
    }

    /**
     * <p>성공 응답을 반환합니다. 첫 번째 인자는 message, 두 번째 인자는 data 에 표시됩니다.</p>
     * <pre>
     *  {
     *      "status" : "success",
     *      "message" : "success message",
     *      "data" : "배열 또는 단일 데이터"
     *  }
     * </pre>
     */
    public <T> ResponseEntity<?> success(ResponseCode responseCode, T data) {
        return get(STATUS_SUCCESS, responseCode.getMessage(), data, null, HttpStatus.OK);
    }

    /**
     * <p>성공 응답을 반환합니다. 전달된 인자는 data 에 표시됩니다.</p>
     * <pre>
     *  {
     *      "status" : "success",
     *      "message" : null,
     *      "data" : "배열 또는 단일 데이터"
     *  }
     * </pre>
     *
     * @param data 응답 바디 data 필드에 포함될 정보
     * @return 응답 객체
     */
    public <T> ResponseEntity<?> success(T data) {
        return get(STATUS_SUCCESS, null, data, null, HttpStatus.OK);
    }

    /**
     * <p>성공 응답을 반환합니다. 전달된 인자는 data 에 표시됩니다.</p>
     * <pre>
     *  {
     *      "status" : "success",
     *      "message" : success message,
     *      "data" : null
     *  }
     * </pre>
     */
    public <T> ResponseEntity<?> success(ResponseCode responseCode) {
        return get(STATUS_SUCCESS, responseCode.getMessage(), null, null, HttpStatus.OK);
    }

    /**
     * <p>성공 응답을 반환합니다.</p>
     * <pre>
     *  {
     *      "status" : "success",
     *      "message" : null,
     *      "data" : null
     *  }
     * </pre>
     *
     * @return 응답 객체
     */
    public <T> ResponseEntity<?> success() {
        return get(STATUS_SUCCESS, null, null, null, HttpStatus.OK);
    }

    public <T> ResponseEntity<?> pagination(Page<T> page) {
        return get(
                STATUS_SUCCESS,
                null,
                page.getContent(),
                page.getNumber() + 1L,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                HttpStatus.OK
        );
    }

    public <T> ResponseEntity<?> slice(Slice<T> slice) {
        return get(
                STATUS_SUCCESS,
                null,
                slice.getContent(),
                (long) slice.getNumber() + 1,
                slice.getSize(),
                slice.hasNext(),
                slice.hasPrevious(),
                HttpStatus.OK
        );
    }

    /**
     * <p>오류 발생 시 실패 응답을 반환합니다.</p>
     * <pre>
     *     {
     *         "status" : "fail",
     *         "message" : "fail message",
     *         "errors" : null
     *     }
     * </pre>
     *
     * @return 응답 객체
     */
    public <T> ResponseEntity<?> fail(ErrorCode errorCode) {
        return get(STATUS_FAIL, errorCode.getMessage(), null, null, errorCode.getStatus());
    }

    /**
     * <p>필드 에러로 인한 실패 응답을 반환합니다.</p>
     * <pre>
     *     {
     *         "status" : "fail",
     *         "message" : fail message,
     *         "errors" : [{error data1}, {error data2} ... ]
     *     }
     * </pre>
     *
     * @param message 응답 바디 message 필드에 포함될 정보
     * @param errors  응답 바디 errors 필드에 포함될 정보
     * @return 응답 객체
     */
    public <E> ResponseEntity<?> fail(String message, E errors, HttpStatus httpStatus) {
        return get(STATUS_FAIL, message, null, errors, httpStatus);
    }

    /**
     * <p>필드 에러로 인한 실패 응답을 반환합니다.</p>
     * <pre>
     *     {
     *         "status" : "fail",
     *         "message" : fail message,
     *         "errors" : [{error data1}, {error data2} ... ]
     *     }
     * </pre>
     *
     * @param errors 응답 바디 errors 필드에 포함될 정보
     * @return 응답 객체
     */
    public ResponseEntity<?> fail(Errors errors, HttpStatus httpStatus) {
        List<FieldError> fieldErrorList = errors.getAllErrors().stream().map(FieldError::new).collect(Collectors.toList());
        return fail(null, fieldErrorList, httpStatus);
    }

    /**
     * <p>필드 에러로 인한 실패 응답을 반환합니다.</p>
     * <pre>
     *     {
     *         "status" : "fail",
     *         "message" : fail message,
     *         "errors" : [{error data1}, {error data2} ... ]
     *     }
     * </pre>
     *
     * @param bindingResult 응답 바디 errors 필드에 포함될 정보를 가진 BindingResult 객체
     * @return 응답 객체
     */
    public ResponseEntity<?> fail(BindingResult bindingResult) {
        return fail(bindingResult);
    }

    /**
     * <p>필드 에러로 인한 실패 응답을 반환합니다.</p>
     * <pre>
     *     {
     *         "status" : "fail",
     *         "message" : null,
     *         "errors" : [{error data1}, {error data2} ... ]
     *     }
     * </pre>
     *
     * @param errors 응답 바디 errors 필드에 포함될 정보
     * @return 응답 객체
     */
    public <E> ResponseEntity<?> fail(ErrorCode errorCode, E errors) {
        return get(STATUS_FAIL, errorCode.getMessage(), null, errors, errorCode.getStatus());
    }

    /**
     * <p>예외 발생 시 에러 응답을 반환합니다.</p>
     * <pre>
     *     {
     *         "status" : "error",
     *         "message" : "Custom ErrorCode Message"
     *     }
     * </pre>
     */
    public <T> ResponseEntity<?> error(ErrorCode errorCode) {
        return get(STATUS_ERROR, errorCode.getMessage(), null, null, errorCode.getStatus());
    }

    /**
     * <p>예외 발생 시 에러 응답을 반환합니다.</p>
     * <pre>
     *     {
     *         "status" : "error",
     *         "message" : "Custom ErrorCode Message"
     *     }
     * </pre>
     */
    public <T> ResponseEntity<?> error(ErrorCode errorCode, String message) {
        return get(STATUS_ERROR, message, null, null, errorCode.getStatus());
    }

    /**
     * <p>성공 응답 객체의 바디</p>
     */
    @Builder
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SucceededBody<T> {

        private String status;
        private String message;
        private T data;
    }

    @Builder
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PagedBody<T> {
        private String status;
        private String message;
        private PagedContent<T> data;

        @Builder
        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class PagedContent<T> {
            private List<T> items;
            private Long page;
            private int size;
            private Long total;
            private Integer totalPages;
            private Boolean first;
            private Boolean last;
        }
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SlicedBody<T> {
        private String status;
        private String message;
        private SlicedContent<T> data;

        @Builder
        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class SlicedContent<T> {
            private List<T> items;
            private Long page;
            private int size;
            private boolean hasNext;
            private boolean hasPrevious;
        }
    }

    /**
     * <p>실패 응답 객체의 바디</p>
     */
    @Builder
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FailedBody<E> {

        private String status;
        private String message;
        private E errors;
    }

    /**
     * <p>오류 응답 객체의 바디</p>
     */
    @Builder
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErroredBody {

        private String status;
        private String message;
    }

    /**
     * <p>필드 에러 출력에 사용할 객체</p>
     */
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FieldError {

        private String field;
        private String message;

        public FieldError(ObjectError objectError) {
            this.field = objectError.getObjectName();
            this.message = objectError.getDefaultMessage();
        }
    }
}