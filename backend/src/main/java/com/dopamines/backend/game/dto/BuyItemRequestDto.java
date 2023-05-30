package com.dopamines.backend.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.SendTo;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyItemRequestDto {
    private int itemId;
}
