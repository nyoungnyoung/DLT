package com.dopamines.backend.fcm.entity;

import com.dopamines.backend.account.entity.Account;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FCM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_id")
    private Long fcmId;

    // 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account account;

    @Column(name = "device_token")
    private String deviceToken;
}
