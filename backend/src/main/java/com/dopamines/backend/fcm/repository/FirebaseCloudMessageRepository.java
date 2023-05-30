package com.dopamines.backend.fcm.repository;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.fcm.entity.FCM;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FirebaseCloudMessageRepository extends JpaRepository<FCM, Long> {

    Optional<FCM> findByAccount(Account account);
    Optional<FCM> findByAccount_AccountId(Long accountId);
}
