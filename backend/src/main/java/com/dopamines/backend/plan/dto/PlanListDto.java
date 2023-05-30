package com.dopamines.backend.plan.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanListDto {
    private Long planId;
    private String title;
    private LocalDate planDate;
    private LocalTime planTime;
    private String location;
    private String address;
    private Double latitude; // 위도
    private Double longitude; // 경도
    private Long diffHours;
    private Long diffMinutes;
    private Integer participantCount;
    private Integer state;
    private List<PlanListParticipantDto> participantList;


}
