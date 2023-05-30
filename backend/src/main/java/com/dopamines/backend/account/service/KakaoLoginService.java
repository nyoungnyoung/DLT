package com.dopamines.backend.account.service;

import com.dopamines.backend.account.dto.AccountRequestDto;
import com.dopamines.backend.account.dto.RoleToUserRequestDto;

import java.util.Map;

public interface KakaoLoginService {
    Long saveAccount(AccountRequestDto dto);
//    Long saveAccount(String email, Long kakaoId);

    Long saveRole(String roleName);
    Long addRoleToUser(RoleToUserRequestDto dto);

    void updateRefreshToken(String username, String refreshToken);

    Map<String, String> refresh(String refreshToken);
}
