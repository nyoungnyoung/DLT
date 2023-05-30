package com.dopamines.backend.game.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private int itemId;
    private int code;
    private int price;
    private boolean bought;
    private boolean worn;

}
