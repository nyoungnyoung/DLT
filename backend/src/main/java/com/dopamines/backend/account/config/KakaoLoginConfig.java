package com.dopamines.backend.account.config;

import com.dopamines.backend.account.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class KakaoLoginConfig {
    private final WebClient webClient;
    private static final String USER_INFO_URI = "http://localhost:8081/account/oauth";
        public Flux<TokenResponseDto> login(String email, Long kakaoId) {
            String uri = USER_INFO_URI;

            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("email", email);
            bodyMap.put("kakaoId", kakaoId.toString());

            Flux<TokenResponseDto> response = webClient.post()
                    .uri(uri)
                    .body(BodyInserters.fromValue(bodyMap))
                    .retrieve()
                    .bodyToFlux(TokenResponseDto.class);

            return response;
        }

}