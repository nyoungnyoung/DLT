package com.dopamines.backend.review.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {

    private Long planId;

    private Long photoId;
    private String photoUrl;
    private LocalDateTime registerTime;

    private List<CommentDto> comments;
}
