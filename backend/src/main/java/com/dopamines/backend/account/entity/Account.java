package com.dopamines.backend.account.entity;

import com.dopamines.backend.common.BaseTimeEntity;
import com.dopamines.backend.friend.entity.Friend;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false, unique = true)
    private String email;

//    @Column(nullable = false, length = 10, unique = true)
    private String nickname;

//    @Column(nullable = false, unique = true)
    private String kakaoId;

//    @Column(nullable = false)
    @Column(length = 2500)
    private String profile;

    private String profileMessage;
    @Column(nullable = false)
    @ColumnDefault("0")
    private int thyme;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int totalIn;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int totalOut;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int accumulatedTime;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    // 지갑 총액
    @Column(nullable = false)
    @ColumnDefault("0")
    private int totalWallet;

    @ManyToMany
    private List<Role> roles = new ArrayList<>();

    private String refreshToken;

    public void updateRefreshToken(String newToken) {
        this.refreshToken = newToken;
    }

    // 친구
    @OneToMany(mappedBy="account")
    private List<Friend> friends = new ArrayList<>();

//    @OneToOne(mappedBy = "my_character_id", cascade = CascadeType.ALL, orphanRemoval = true)
//    private MyCharacter myCharacter;
//

//    @PostConstruct
//    public void setMyCharacter(MyCharacter myCharacter) {
//        this.myCharacter = myCharacter;
////        myCharacter.setAccount(this);
//    }
}
