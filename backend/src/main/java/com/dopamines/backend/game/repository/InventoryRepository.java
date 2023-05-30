package com.dopamines.backend.game.repository;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.game.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Inventory findByAccount_Email(String email);
    List<Inventory> findAllByAccount(Account acccount);
    List<Inventory> findAllByAccount_Email(String email);
}
