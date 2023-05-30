package com.dopamines.backend.game.service;

import com.dopamines.backend.game.dto.MyCharacterDto;

public interface MyCharacterService {
    MyCharacterDto getMyCharacter(String email);
    void wearItem(String email, MyCharacterDto myCharacterDto);
}
