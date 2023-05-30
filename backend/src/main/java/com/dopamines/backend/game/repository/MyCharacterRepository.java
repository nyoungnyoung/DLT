package com.dopamines.backend.game.repository;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.game.entity.MyCharacter;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MyCharacterRepository extends JpaRepository<MyCharacter, Long> {
    MyCharacter findByAccount(Account account);
    MyCharacter findMyCharacterByAccount_Email(String email);
}
