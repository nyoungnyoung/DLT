package com.dopamines.backend.plan.entity;


import com.dopamines.backend.account.entity.Account;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;

    // 약속
    @ManyToOne(fetch = FetchType.LAZY)  // 1:N
    @JoinColumn(name="plan_id") //Join 기준
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Plan plan;

    // 참여자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account account;

    @Column(name = "is_host")
    private Boolean isHost;

    @Column(name = "is_arrived")
    @ColumnDefault("false")
    private Boolean isArrived;

    @Column(name = "arrival_time")
    private LocalTime arrivalTime;

    @Column(name = "late_time")
    @ColumnDefault("0")
    private Long lateTime;

    @Column(name = "transaction_money")
    @ColumnDefault("0")
    private int transactionMoney;

    @Column(name = "thyme")
    @ColumnDefault("0")
    private int thyme;


}
