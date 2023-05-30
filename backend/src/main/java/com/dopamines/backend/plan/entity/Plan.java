package com.dopamines.backend.plan.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "title")
    private String title;

    @Column(name = "plan_date")
    private LocalDate planDate;

    @Column(name = "plan_time")
    private LocalTime planTime;

    // 주소 이름
    @Column(name = "location")
    private String location;

    // 주소
    @Column(name = "address")
    private String address;

    // 위도
    @Column(name = "latitude")
    private Double latitude;

    // 경도
    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "cost")
    private Integer cost;

    @ColumnDefault("0")
    private int state; // 0: 기본, 1: 위치공유(30분 전~약속시간), 2: 게임 활성화(약속시간~1시간 후), 3: 약속 종료(1시간 이후)

    @Column(name = "is_settle")
    @ColumnDefault("false")
    private Boolean isSettle;
}
