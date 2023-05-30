package com.dopamines.backend.review.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoDetailDto {
    private Long planId;
    private Long photoId;
    private String photoUrl;
    private LocalDateTime registerTime;
}
