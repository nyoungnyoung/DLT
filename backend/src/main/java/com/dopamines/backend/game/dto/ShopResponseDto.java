package com.dopamines.backend.game.dto;


import com.dopamines.backend.game.entity.Inventory;
import lombok.*;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponseDto {
    private int thyme;
    private MyCharacterDto myCharacter;
    private HashMap<String, List<ItemDto>> items;

}
