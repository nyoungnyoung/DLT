package com.dopamines.backend.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPageDto {

    private Long accountId;

    private String nickname;

    private String profile;

    private int averageArrivalTime;

    private int latenessRate;

    private int totalCost;

}
