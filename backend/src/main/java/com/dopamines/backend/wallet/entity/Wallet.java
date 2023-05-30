package com.dopamines.backend.wallet.entity;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.plan.entity.Plan;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Wallet_id")
    private Long WalletId;

    // 참여자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account account;

    // 약속 (null 허용) - 목록에 제목을 불러오기 때문에 필요
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="plan_id")
    private Plan plan;

    private Integer money;

//    @Column(name = "transaction_time")
//    private LocalDateTime transactionTime;
    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "transaction_time", columnDefinition = "TIME")
    private LocalTime transactionTime;

    private Integer type; // 0: 충전, 1: 출금, 2: 약속으로 얻은 지각비, 3: 약속으로 잃은 지각비

    // 목록에 누적 금액을 불러오기 때문에 필요
    @Column(name = "total_money")
    private Integer totalMoney;

    private String method;

    private String receipt;

}

