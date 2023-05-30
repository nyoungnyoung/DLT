package com.dopamines.backend.plan.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameMoneyDto {
    private Long planId;
    private Integer totalPayment;
}
