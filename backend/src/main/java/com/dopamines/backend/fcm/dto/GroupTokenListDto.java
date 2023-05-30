package com.dopamines.backend.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GroupTokenListDto {
    private List<String> groupToken;
}
