package com.dopamines.backend.security;

//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import com.dopamines.backend.account.service.KakaoLoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dopamines.backend.security.JwtConstants.*;
import static com.dopamines.backend.security.JwtConstants.RT_HEADER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final KakaoLoginService accountService;
    private final AccountRepository accountRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();

        Optional<Account> account = accountRepository.findByEmail(user.getUsername());
        String nickname = null;
        if(account.isPresent()){
            nickname = account.get().getNickname();
        }

        String accessToken = Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + AT_EXP_TIME))
                .claim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET.getBytes())
                .compact();
        String refreshToken = Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + RT_EXP_TIME))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET.getBytes())
                .compact();

        // Refresh Token DB에 저장
        accountService.updateRefreshToken(user.getUsername(), refreshToken);

        // Access Token , Refresh Token 프론트 단에 Response Header로 전달
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.setHeader(AT_HEADER, accessToken);
        response.setHeader(RT_HEADER, refreshToken);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(AT_HEADER, accessToken);
        responseMap.put(RT_HEADER, refreshToken);
        responseMap.put("nickname", nickname);
        new ObjectMapper().writeValue(response.getWriter(), responseMap);
    }
}

