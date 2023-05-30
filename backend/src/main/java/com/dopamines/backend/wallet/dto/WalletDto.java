package com.dopamines.backend.wallet.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletDto {
    private int total;
    private Map<LocalDate, List<WalletDetailDto>> details;


}
