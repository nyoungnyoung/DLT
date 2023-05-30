package com.dopamines.backend.account.repository;

import com.dopamines.backend.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<Account> findByNickname(String nickname);

    Optional<Account> findById(Long accountId);

    List<Account> findByNicknameContaining(String keyword);
}
