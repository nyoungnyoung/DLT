package com.dopamines.backend.wallet.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementDto {
    private Long accountId;
    private String nickName;
    private Integer paymentAmount;
    private String deviceToken;
}
