package com.dopamines.backend.test.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ObjectDto {
    Long id;
    String name;
}