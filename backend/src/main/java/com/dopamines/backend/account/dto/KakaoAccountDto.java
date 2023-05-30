package com.dopamines.backend.account.dto;


import lombok.Data;

@Data
public class KakaoAccountDto {
    private Boolean has_email;
    private Boolean email_needs_agreement;
    private Boolean is_email_valid;
    private Boolean is_email_verified;
    private String email;
    private KakaoProfileDto profile;
}
