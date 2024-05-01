package com.example.sabujak.common.response;

public record Response<T>(
        String status,
        T data,
        String message
) {
    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String FAIL_STATUS = "FAIL";

    public static <T> Response<T> success() {
        return new Response<>(SUCCESS_STATUS, null, null);
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(SUCCESS_STATUS, data, null);
    }

    public static <T> Response<T> fail(String message) {
        return new Response<>(FAIL_STATUS, null, message);
    }
}
