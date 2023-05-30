package com.dopamines.backend.game.controller;

import com.dopamines.backend.game.dto.ExitGameRequestDto;
import com.dopamines.backend.game.dto.GameResponseDto;
import com.dopamines.backend.game.dto.UpdateMoneyRequestDto;
import com.dopamines.backend.game.dto.UpdateMoneyResponseDto;
import com.dopamines.backend.game.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GameController {
    private final GameService gameService;
    @GetMapping("/enter/{planId}")
    public ResponseEntity<GameResponseDto> enterGame(HttpServletRequest request, @PathVariable("planId") Long planId){
        log.info("planId: " + planId);
        String email = request.getRemoteUser();
        try {
            GameResponseDto res = gameService.enterGame(email, planId);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/money")
    public ResponseEntity<UpdateMoneyResponseDto> updateMoney(HttpServletRequest request, @RequestBody UpdateMoneyRequestDto dto) {
        String email = request.getRemoteUser();
        int result = gameService.updateMoney(email, dto.getRoomNumber(), dto.getMoney());
        return ResponseEntity.ok(new UpdateMoneyResponseDto(result));
    }

    @PostMapping("/exit")
    public ResponseEntity exitGame(HttpServletRequest request, ExitGameRequestDto exitGameRequest){
        String email = request.getRemoteUser();
        log.info("exitGameRequest.getPlanId(): " + exitGameRequest.getPlanId());
        log.info("exitGameRequest.getTransactionMoney(): " + exitGameRequest.getTransactionMoney());
        try {
            gameService.exitGame(email, exitGameRequest.getPlanId(), exitGameRequest.getTransactionMoney());
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


}
