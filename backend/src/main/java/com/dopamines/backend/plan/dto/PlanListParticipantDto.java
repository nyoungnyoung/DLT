package com.dopamines.backend.plan.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanListParticipantDto {

    private Long accountId;
    private String profile;
}
