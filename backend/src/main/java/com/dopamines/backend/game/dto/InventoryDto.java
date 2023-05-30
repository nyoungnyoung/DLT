package com.dopamines.backend.game.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {
    private List<Integer> bodies;
    private List<Integer> bodyParts;
    private List<Integer> eyes;
    private List<Integer> gloves;
    private List<Integer> mouthAndNoses;
    private List<Integer> tails;
}
