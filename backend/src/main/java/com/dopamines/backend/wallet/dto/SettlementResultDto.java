package com.dopamines.backend.wallet.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementResultDto {

    private boolean success;
    private List<SettlementDto> participants;
}
