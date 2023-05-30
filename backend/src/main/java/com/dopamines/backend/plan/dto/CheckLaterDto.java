package com.dopamines.backend.plan.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckLaterDto {

    private Long accountId;
    private Boolean IsLate;
}
