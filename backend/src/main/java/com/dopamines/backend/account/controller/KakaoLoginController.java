package com.dopamines.backend.account.controller;

import com.dopamines.backend.account.config.KakaoLoginConfig;
import com.dopamines.backend.account.config.KakaoUserInfo;
import com.dopamines.backend.account.dto.*;
import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import com.dopamines.backend.account.service.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static com.dopamines.backend.security.JwtConstants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequestMapping("/account")
@RequiredArgsConstructor
@RestController
public class KakaoLoginController {

    private final KakaoLoginService accountService;
    @Autowired
    private final KakaoUserInfo kakaoUserInfo;
    @Autowired
    private final KakaoLoginConfig kakaoLoginConfig;
    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/oauth")
    @ResponseBody
    public ResponseEntity<AccountRequestDto> kakaoOauth(@RequestBody KakaoOauthDto kakaoOauthDto) throws IOException {
        String code = kakaoOauthDto.getCode();
        log.info("code: " + code);

        KakaoUserInfoResponseDto userInfo = kakaoUserInfo.getUserInfo(code);

        String email = userInfo.getKakao_account().getEmail();
        Long kakaoId = userInfo.getId();
        String nickname = userInfo.getKakao_account().getProfile().getNickname();

        Optional<Account> optional = accountRepository.findByEmail(email);

        Boolean signup;

        // 회원이 아니면 회원가입
        if (optional.isEmpty()) {
           signup = false;
            log.info("signup: " +signup);

            // default 프사 이미지 url 생기면 판별 후 profile에 url 추가 필수!!
//            signup(accountRequestDto);
        } else {
            signup = true;
            log.info("signup: " +signup);
        }

        AccountRequestDto accountRequestDto = new AccountRequestDto(signup, email, kakaoId.toString(), nickname, userInfo.getKakao_account().getProfile().getProfile_image_url());

        log.info("accountRequestDto: " + accountRequestDto.getSignup());
        return ResponseEntity.ok(accountRequestDto);

    }


    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@RequestParam String email, @RequestParam String kakaoId, @RequestParam String nickname, @RequestParam String profile) {
//    public ResponseEntity<Long> signup(@RequestBody AccountRequestDto dto) {
        AccountRequestDto dto = new AccountRequestDto(false, email, kakaoId, nickname, profile);
        return ResponseEntity.ok(accountService.saveAccount(dto));
    }

    @PostMapping("/role")
    public ResponseEntity<Long> saveRole(@RequestBody String roleName) {
        return ResponseEntity.ok(accountService.saveRole(roleName));
    }

    @PostMapping("/userrole")
    public ResponseEntity<Long> addRoleToUser(@RequestBody RoleToUserRequestDto dto) {
        return ResponseEntity.ok(accountService.addRoleToUser(dto));
    }

    @GetMapping("/my")
    public ResponseEntity<String> my() {
        return ResponseEntity.ok("My");
    }

    @GetMapping("/admin")
    public ResponseEntity<String> admin() {
        return ResponseEntity.ok("Admin");
    }

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
            throw new RuntimeException("AccountApiController에서 JWT Token이 존재하지 않습니다.");
        }
        String refreshToken = authorizationHeader.substring(TOKEN_HEADER_PREFIX.length());
        Map<String, String> tokens = accountService.refresh(refreshToken);
        response.setHeader(AT_HEADER, tokens.get(AT_HEADER));
        if (tokens.get(RT_HEADER) != null) {
            response.setHeader(RT_HEADER, tokens.get(RT_HEADER));
        }
        return ResponseEntity.ok(tokens);
    }
}
