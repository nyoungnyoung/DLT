package com.dopamines.backend.plan.repository;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.plan.entity.Participant;
import com.dopamines.backend.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByPlan_PlanId(Long planId);

    List<Participant> findByPlan(Plan plan);
    List<Participant> findByAccount(Account account);

    Optional<Participant> findByAccount_Email(String email);
    Optional<Participant> findByPlanAndAccount(Plan plan, Account account);
    List<Participant> findByAccountAndPlanPlanDateBetween(Account account, LocalDate startDate, LocalDate endDate);
    Integer countByPlan(Plan plan);

    List<Participant> findAllByPlan(Plan plan);
}
