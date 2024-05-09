package com.example.sabujak.common.response;

public record Response<T>(
        String status,
        String errorCode,
        T data,
        String message
) {
    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String FAIL_STATUS = "FAIL";

    public static <T> Response<T> success() {
        return new Response<>(SUCCESS_STATUS, null, null, null);
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(SUCCESS_STATUS, null, data, null);
    }

    public static Response<String> fail(String errorCode, String message) {
        return new Response<>(FAIL_STATUS, errorCode, null, message);
    }
}
