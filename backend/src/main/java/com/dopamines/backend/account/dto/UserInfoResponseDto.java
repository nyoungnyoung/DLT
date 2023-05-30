package com.dopamines.backend.account.dto;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class UserInfoResponseDto {
    private long id;
    private String nickname;
    private String profile;

}
