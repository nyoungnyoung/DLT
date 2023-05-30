package com.dopamines.backend.plan.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameResultMoneyDto {

    private Long planId;
    private Long accountId;
    private Integer planCost;
    private Boolean isLate;
    private Integer getGameMoney;
    private Integer getBalance;
    private Integer finalAmount;
    private Integer participantCount;
    private Integer rank;
    private Integer thyme;

}
