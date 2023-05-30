package com.dopamines.backend.game.controller;

import com.dopamines.backend.game.dto.BuyItemRequestDto;
import com.dopamines.backend.game.dto.ItemDto;
import com.dopamines.backend.game.dto.ShopResponseDto;
import com.dopamines.backend.game.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ItemController {
    private final ItemService itemService;
    @GetMapping("/all")
    public ResponseEntity<HashMap<String, List<ItemDto>>> getItems(HttpServletRequest request) {
        String email = request.getRemoteUser();
        return ResponseEntity.ok(itemService.getItems(email));
    }

    @GetMapping("/getShop")
    public ResponseEntity<ShopResponseDto> getShop(HttpServletRequest request) {
        String email = request.getRemoteUser();
        return ResponseEntity.ok(itemService.getShop(email));
    }
    @PostMapping("/buy")
    public ResponseEntity buyItem(HttpServletRequest reqest, @RequestBody BuyItemRequestDto dto) {
        String email = reqest.getRemoteUser();
        try {
            itemService.buyItem(email, dto.getItemId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.info(e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.badRequest().build();

        }


    }

}
