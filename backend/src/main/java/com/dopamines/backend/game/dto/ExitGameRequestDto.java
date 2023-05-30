package com.dopamines.backend.game.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExitGameRequestDto {

    private Long planId;
    private int transactionMoney;

}
