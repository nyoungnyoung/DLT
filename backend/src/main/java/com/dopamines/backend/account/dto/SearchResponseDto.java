package com.dopamines.backend.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchResponseDto {
    private Long accountId;
    private String nickname;
    private String profile;
    private String profileMessage;
}
