package com.dopamines.backend.game.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponseDto {
    private String nickname;
    private MyCharacterDto myCharacter;
    private int originTotalMoney;
    private int totalMoney;
    private int transactionMoney;

}
