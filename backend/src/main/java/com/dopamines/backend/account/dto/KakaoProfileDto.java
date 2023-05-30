package com.dopamines.backend.account.dto;

import lombok.Data;

@Data
public class KakaoProfileDto {
    private String nickname;
    private Boolean is_default_image;
    private String profile_image_url;

}

