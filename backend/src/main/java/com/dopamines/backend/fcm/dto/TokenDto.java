package com.dopamines.backend.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.beans.ConstructorProperties;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {
    private String deviceToken;
}
