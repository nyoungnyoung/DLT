package com.dopamines.backend.game.entity;


import com.dopamines.backend.account.entity.Account;
import lombok.*;

import javax.annotation.PostConstruct;
import javax.persistence.*;


@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MyCharacter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myCharacterId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

//    @Column(nullable = false)
//    @ColumnDefault("1")
    private int body = 1;

//    @ColumnDefault("0")
    private int bodyPart = 0;

//    @ColumnDefault("1")
    private int eye = 1;

//    @ColumnDefault("0")
    private int gloves = 0;

//    @ColumnDefault("1")
    private int mouthAndNose = 1;

//    @ColumnDefault("0")
    private int tail = 0;

    @PostConstruct
    public void setAccount(Account account) {
        this.account = account;
    }
}
