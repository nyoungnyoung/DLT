package com.dopamines.backend.friend.service;

import com.dopamines.backend.friend.dto.FriendResponseDto;
import com.dopamines.backend.friend.entity.Friend;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FriendService {
    Map<String, List<FriendResponseDto>> getFriendList(String email);
    FriendResponseDto addFriend(String email, Long friendId);
    FriendResponseDto acceptFriend(String email, Long friendId);
    FriendResponseDto denyFriend(String email, Long friendId);
    FriendResponseDto deleteFriend(String email, Long friendId);
}

