package com.dopamines.backend.webSocket.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class CreateRoomDto {
    private String roomId;
    private String state;
}
