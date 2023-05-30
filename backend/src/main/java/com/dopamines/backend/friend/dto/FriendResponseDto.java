package com.dopamines.backend.friend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Builder
public class FriendResponseDto {
     private Long friendId;
     private String profile;
     private String nickname;
     private String profileMessage;
     private int status; // 1친구 신청 한 상태 2친구 신청 받은 상태 3친구 4아무사이 아님
}
