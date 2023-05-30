package com.dopamines.backend.account.dto;

import lombok.Data;

@Data
public class TokenResponseDto {

    private String access_token;
    private String refresh_token;

}
