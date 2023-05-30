package com.dopamines.backend.account.dto;


import lombok.Data;

@Data
public class KakaoUserInfoResponseDto {
    private Long id;
    private String connected_at;
    private KakaoAccountDto kakao_account;
}
