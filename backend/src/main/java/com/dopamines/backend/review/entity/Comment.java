package com.dopamines.backend.review.entity;


import com.dopamines.backend.plan.entity.Participant;
import com.dopamines.backend.plan.entity.Plan;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    // 약속
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="plan_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Participant participant;

    private String content;

    @Column(name = "register_dt")
    private LocalDateTime registerTime;

    @Column(name = "update_dt")
    private LocalDateTime updateTime;

}
