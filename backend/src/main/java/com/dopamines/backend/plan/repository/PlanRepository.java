package com.dopamines.backend.plan.repository;

import com.dopamines.backend.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findById(Long planId);

    List<Plan> findByPlanTimeBetween(LocalTime startTime, LocalTime endTime);

    List<Plan> findByPlanDateBetween(LocalDate startDate, LocalDate endDate);




}

