package com.dopamines.backend.account.controller;


import com.dopamines.backend.account.dto.MyPageDto;
import com.dopamines.backend.account.dto.NicknameProfileDto;
import com.dopamines.backend.account.dto.SearchResponseDto;
import com.dopamines.backend.account.dto.UserInfoResponseDto;
import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import com.dopamines.backend.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping("/account")
@RequiredArgsConstructor
@RestController
public class AccountController {
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    @PutMapping("/nickname")
    public ResponseEntity<Account> editNickname(HttpServletRequest request, @RequestParam String nickname) {
        String user = request.getRemoteUser();
        log.info("nickname에서 찍는 user: " + user);
        //        Long userId = (Long)request.getSession().getAttribute(WebKeys.USER_ID);
        return ResponseEntity.ok(accountService.editNickname(user, nickname));
    }


    @PutMapping("/profileMessage")
    public ResponseEntity<Account> editprofileMessage(HttpServletRequest request, @RequestParam String profileMessage) {
        String email = request.getRemoteUser();
        log.info("profileMessage 찍는 user: " + email);
        return ResponseEntity.ok(accountService.editProfileMessage(email, profileMessage));
    }

    @PutMapping("/delete")
    public ResponseEntity deleteAccount(HttpServletRequest request){
        String email = request.getRemoteUser();
        accountService.deleteAccount(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchResponseDto>> searchNickname(@RequestParam String keyword){
        return ResponseEntity.ok(accountService.searchNickname(keyword));
    }

    @GetMapping("/participant")
    public ResponseEntity<NicknameProfileDto> getNicknameProfile(HttpServletRequest request, Long accountId){
        return ResponseEntity.ok(accountService.getNicknameProfile(accountId));
    }

    @GetMapping("/id")
    public ResponseEntity<UserInfoResponseDto>  getAccountId(HttpServletRequest request) {
        String email = request.getRemoteUser();
        Optional<Account> account = accountRepository.findByEmail(email);
        if(account.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            log.info("account.get().getAccountId(): " + account.get().getAccountId());
            log.info("account.get().getNickname(): " + account.get().getNickname());
            log.info("account.get().getProfile(): " + account.get().getProfile());

            UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto(account.get().getAccountId(), account.get().getNickname(), account.get().getProfile());
            return ResponseEntity.ok(userInfoResponseDto);
        }

    }
}
