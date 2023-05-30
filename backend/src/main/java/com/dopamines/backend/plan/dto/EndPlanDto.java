package com.dopamines.backend.plan.dto;

import lombok.*;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndPlanDto {

    private Long planId;
    private String title;
    private LocalDate planDate;
    private LocalTime planTime;
    private String location;
    private String address;
    private Double latitude; // 위도
    private Double longitude; // 경도
    private Integer cost;
    private Integer state;
    private Boolean isSettle; // 정산 유무
    private MyEndPlanDto myDetail;
    private List<EndPlanParticipantDto> EndPlanParticipantDto;

}
