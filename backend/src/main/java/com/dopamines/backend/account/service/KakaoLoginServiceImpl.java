package com.dopamines.backend.account.service;

import com.dopamines.backend.account.dto.AccountRequestDto;
import com.dopamines.backend.account.dto.RoleToUserRequestDto;
import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.entity.Role;
import com.dopamines.backend.account.repository.AccountRepository;
import com.dopamines.backend.account.repository.RoleRepository;
import com.dopamines.backend.game.entity.Inventory;
import com.dopamines.backend.game.entity.MyCharacter;
import com.dopamines.backend.game.repository.InventoryRepository;
import com.dopamines.backend.game.repository.ItemRepository;
import com.dopamines.backend.game.repository.MyCharacterRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dopamines.backend.security.JwtConstants.*;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class KakaoLoginServiceImpl implements KakaoLoginService, UserDetailsService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final MyCharacterRepository myCharacterRepository;
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("UserDetailsService - loadUserByUsername : 사용자를 찾을 수 없습니다."));

        List<SimpleGrantedAuthority> authorities = account.getRoles()
                .stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

        return new User(account.getEmail(), account.getKakaoId(), authorities);
    }

    @Override
    public Long saveAccount(AccountRequestDto dto) {
        log.info("saveAccount에서 찍는 email " + dto.getEmail());
        log.info("saveAccount에서 찍는 kakaoId " + dto.getKakaoId());
        log.info("saveAccount에서 찍는 nickname " + dto.getNickname());

        validateDuplicateUsername(dto);
        dto.encodePassword(passwordEncoder.encode(dto.getKakaoId()));

        Account account = dto.toEntity();
        Long accountId = accountRepository.save(account).getAccountId();

        MyCharacter myCharacter = MyCharacter.builder()
//                .name(dto.getNickname() + "의 캐릭터")
//                .level(1)
//                .experience(0)
                .body(1)
                .eye(17)
                .mouthAndNose(38)
                .account(account)
                .build();

        myCharacterRepository.save(myCharacter);


        Inventory inventory1 = new Inventory();
        inventory1.setAccount(account);
        inventory1.setItem(itemRepository.findById(1).get());
        inventoryRepository.save(inventory1);

        Inventory inventory2 = new Inventory();
        inventory2.setAccount(account);
        inventory2.setItem(itemRepository.findById(17).get());
        inventoryRepository.save(inventory2);

        Inventory inventory3 = new Inventory();
        inventory3.setAccount(account);
        inventory3.setItem(itemRepository.findById(38).get());
        inventoryRepository.save(inventory3);



        return accountId;
    }

    private void validateDuplicateUsername(AccountRequestDto dto) {
        if (accountRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("이미 존재하는 ID입니다.");
        }
    }

    @Override
    public Long saveRole(String roleName) {
        validateDuplicateRoleName(roleName);
        return roleRepository.save(new Role(roleName)).getId();
    }

    private void validateDuplicateRoleName(String roleName) {
        if (roleRepository.existsByName(roleName)) {
            throw new RuntimeException("이미 존재하는 Role입니다.");
        }
    }

    @Override
    public Long addRoleToUser(RoleToUserRequestDto dto) {
        Account account = accountRepository.findByEmail(dto.getUsername()).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Role role = roleRepository.findByName(dto.getRoleName()).orElseThrow(() -> new RuntimeException("ROLE을 찾을 수 없습니다."));
        account.getRoles().add(role);
        return account.getAccountId();
    }

    // =============== TOKEN ============ //

    @Override
    public void updateRefreshToken(String username, String refreshToken) {
        Account account = accountRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        account.updateRefreshToken(refreshToken);
    }
    @Override
    public Map<String, String> refresh(String refreshToken) {

        // === Refresh Token 유효성 검사 === //
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(JWT_SECRET.getBytes())).build().parseClaimsJws(refreshToken);
        Claims claims = claimsJws.getBody();

        // === Access Token 재발급 === //
        long now = System.currentTimeMillis();
        String username = claims.getSubject();
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        if (!account.getRefreshToken().equals(refreshToken)) {
//            throw new JWTVerificationException("유효하지 않은 Refresh Token 입니다.");
              throw new RuntimeException("유효하지 않은 Refresh Token 입니다.");

        }
        String accessToken = Jwts.builder()
                .setSubject(account.getEmail())
                .setExpiration(new Date(now + AT_EXP_TIME))
                .claim("roles", account.getRoles().stream().map(Role::getName)
                        .collect(Collectors.toList()))
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()))
                .compact();
        Map<String, String> accessTokenResponseMap = new HashMap<>();

        // === 현재시간과 Refresh Token 만료날짜를 통해 남은 만료기간 계산 === //
        // === Refresh Token 만료시간 계산해 1개월 미만일 시 refresh token도 발급 === //
        long refreshExpireTime = claims.getExpiration().getTime();
        long diffDays = (refreshExpireTime - now) / 1000 / (24 * 3600);
        long diffMin = (refreshExpireTime - now) / 1000 / 60;
        if (diffMin < 5) {
            String newRefreshToken = Jwts.builder()
                    .setSubject(account.getEmail())
                    .setExpiration(new Date(now + RT_EXP_TIME))
                    .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()))
                    .compact();
            accessTokenResponseMap.put(RT_HEADER, newRefreshToken);
            account.updateRefreshToken(newRefreshToken);
        }

        accessTokenResponseMap.put(AT_HEADER, accessToken);
        return accessTokenResponseMap;
    }

}
