package com.example.sabujak.security.dto.request;

public record VerifyRequestDto() {

    public record Email(String emailAddress) {
    }

    public record Phone(String phoneNumber) {
    }

    public record EmailCode(String emailAddress, String code) {
    }
}
