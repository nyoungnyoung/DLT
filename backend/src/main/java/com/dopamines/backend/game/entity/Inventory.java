package com.dopamines.backend.game.entity;

import com.dopamines.backend.account.entity.Account;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
@Slf4j
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="item_id")
    private Item item;

}
