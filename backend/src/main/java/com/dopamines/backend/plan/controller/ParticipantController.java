package com.dopamines.backend.plan.controller;

import com.dopamines.backend.plan.dto.GameOverRequestDto;
import com.dopamines.backend.plan.dto.GameResultMoneyDto;
import com.dopamines.backend.plan.service.ParticipantService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/participant")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Api(value = "participant", description = "참가자 정보를 관리하는 컨트롤러입니다.")
public class ParticipantController {

    private final ParticipantService participantService;


    @PostMapping("/registerGetMoney")
    @Operation(summary = "게임에서 획득한 지각비 정보를 입력하는 api 입니다.", description = "게임이 끝난후 게임 결과에 따라 planId와 getMoney, balance를 입력하여 각 참가자의 거래 금액을 업데이트합니다.<br>" +
            "getMoney는 일찍 온 사람, 지각자가 게임에서 획득한 금액을 입력합니다. 지각자가 획득한 금액은 지각비에서 제외됩니다.<br>" +
            "balance는 총 지각비 중 참가자들이 획득하지 못 한 남은 잔액을 입력합니다. 마지막 지각자가 도착하여 못 찾은 돈은 전체 참가자가 N빵하여 획득하게 됩니다.")
    public ResponseEntity<GameResultMoneyDto> registerGetMoney(
            HttpServletRequest request,
            @RequestBody GameOverRequestDto dto
//            @RequestParam("balance") Integer balance
    ){
        try {
            String userEmail = request.getRemoteUser();
            GameResultMoneyDto gameResultMoneyDto = participantService.registerGetMoney(userEmail, dto.getPlanId(), dto.getGetGameMoney());
            return ResponseEntity.ok(gameResultMoneyDto);
        } catch (IllegalArgumentException e) {
            log.error("게임 결과 반영 API 호출 중 예외 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("게임 결과 반영 API 호출 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
