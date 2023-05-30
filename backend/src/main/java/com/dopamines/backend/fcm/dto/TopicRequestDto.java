package com.dopamines.backend.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopicRequestDto {
    private String topic;
    private String title;
    private String body;
    private String planId;
    private String type;
}
