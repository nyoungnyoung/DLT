package com.dopamines.backend.game.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCharacterDto {

    private int bodies;
    private int bodyParts;
    private int eyes;
    private int gloves;
    private int mouthAndNoses;
    private int tails;


}
