package com.dopamines.backend.game.repository;

import com.dopamines.backend.game.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByCategory(String category);

    Optional<Item> findByItemId(int itemId);
    Optional<Item> findByCategoryAndCode(String category, int code);


}
