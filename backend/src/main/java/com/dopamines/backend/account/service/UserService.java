package com.dopamines.backend.account.service;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    AccountRepository accountRepository;

    public Account findByAccountId(Long accountId) {
        Optional<Account> optionalUser = accountRepository.findById(accountId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new RuntimeException("존재하지 않는 회원입니다.");
        }
    }

    public Account findByEmail(String email) {
        Optional<Account> optionalUser = accountRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new RuntimeException("존재하지 않는 회원입니다.");
        }
    }


}
