package com.example.sabujak.common.response;

public record CommonResponse<T>(
        String status,
        T data,
        String message
) {
    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String FAIL_STATUS = "FAIL";

    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(SUCCESS_STATUS, null, null);
    }

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(SUCCESS_STATUS, data, null);
    }

    public static <T> CommonResponse<T> fail(String message) {
        return new CommonResponse<>(FAIL_STATUS, null, message);
    }
}
