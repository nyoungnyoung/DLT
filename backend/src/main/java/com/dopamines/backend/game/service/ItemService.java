package com.dopamines.backend.game.service;

import com.dopamines.backend.game.dto.ItemDto;
import com.dopamines.backend.game.dto.ShopResponseDto;
import com.dopamines.backend.game.entity.Inventory;
import com.dopamines.backend.game.entity.Item;
import org.apache.tomcat.websocket.AuthenticationException;

import java.util.HashMap;
import java.util.List;

public interface ItemService {


    ShopResponseDto getShop(String email);

    HashMap<String, List<ItemDto>> getItems(String email);

    void buyItem(String email, int itemId) throws Exception;

    List<Integer> inventoryListToItemIdList(List<Inventory> inventoryList);
}
