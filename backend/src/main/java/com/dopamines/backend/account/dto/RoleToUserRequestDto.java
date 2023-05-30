package com.dopamines.backend.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleToUserRequestDto {
    private String username;
    private String roleName;
}
