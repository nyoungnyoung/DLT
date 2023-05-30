package com.dopamines.backend.account.dto;

import com.dopamines.backend.account.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDto {
    private Boolean signup;
    private String email;
    private String kakaoId;
    private String nickname;
    private  String profile;

    public Account toEntity() {
        return Account.builder()
                .email(email)
                .kakaoId(kakaoId)
                .nickname(nickname)
                .profile(profile)
                .thyme(500)
                .totalIn(0)
                .totalOut(0)
                .accumulatedTime(0)
                .isDeleted(false)
                .build();
    }

    public void encodePassword(String encodedPassword) {
        this.kakaoId = encodedPassword;
    }
}
