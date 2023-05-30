package com.dopamines.backend.plan.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class EndPlanParticipantDto {

    private Long accountId;
    private String nickname;
    private String profile;
    private Long lateTime;
    private Integer designation; // 0 보통, 1 일찍, 2 지각
    private Boolean paymentAvailability;
    private String deviceToken;
}
