package com.dopamines.backend.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenRequestDto {
    private String title;
    private String body;
    private String planId;
    private String type;
    private String targetToken;
}
