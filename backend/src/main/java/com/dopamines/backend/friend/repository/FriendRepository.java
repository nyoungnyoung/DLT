package com.dopamines.backend.friend.repository;

import com.dopamines.backend.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository  extends JpaRepository<Friend, Long> {
    List<Friend> findAllByAccount_AccountId(Long accountId);
    Friend findAllById(Long id);
    List<Friend> findAllByFriendIdAndAccount_AccountId(Long friendId, Long accountId);
    Optional<Friend> findByFriendIdAndAccount_AccountId(Long friendId, Long accountId);
}
