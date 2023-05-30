package com.dopamines.backend.game.dto;

import lombok.*;

import java.util.List;
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemByCategoryDto {
    private List<ItemDto> bodies;
    private List<ItemDto> bodyParts;
    private List<ItemDto> eyes;
    private List<ItemDto> gloves;
    private List<ItemDto> mouthAndNoses;
    private List<ItemDto> tails;
}
