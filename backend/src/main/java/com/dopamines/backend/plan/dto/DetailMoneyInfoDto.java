package com.dopamines.backend.plan.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailMoneyInfoDto {
    private Long planId;
    private Integer totalPayment;
    private Integer laterCount;
    private Integer cost;
    private List<CheckLaterDto> participants;
}
