package com.dopamines.backend.review.entity;

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
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long photoId;

    // 약속
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="plan_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Plan plan;

    @Column(name = "photo_url", length = 2500)
    private String photoUrl;

    @Column(name = "register_dt")
    private LocalDateTime registerTime;
}
