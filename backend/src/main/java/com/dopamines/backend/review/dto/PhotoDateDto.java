package com.dopamines.backend.review.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoDateDto {

    private Long photoId;
    private Long planId;
    private String photoUrl;
    private LocalTime planTime;
}

