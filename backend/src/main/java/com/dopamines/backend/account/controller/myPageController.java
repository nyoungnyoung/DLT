package com.dopamines.backend.account.controller;

import com.dopamines.backend.account.dto.MyPageDto;
import com.dopamines.backend.account.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Api(value = "my", description = "마이페이지를 관리하는 컨트롤러입니다.")
public class myPageController {
    private final AccountService accountService;
    
    @GetMapping("/info")
    @Operation(summary = "마이페이지 정보를 불러오는 api 입니다.")
    public ResponseEntity<MyPageDto> getMyInfo(HttpServletRequest request){

        try {
            String userEmail = request.getRemoteUser();
            MyPageDto myPageDto = accountService.getMyInfo(userEmail);
            return ResponseEntity.ok(myPageDto);
        } catch (IllegalArgumentException e) {
            log.error("API 호출 중 예외 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("API 호출 중 예외 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
