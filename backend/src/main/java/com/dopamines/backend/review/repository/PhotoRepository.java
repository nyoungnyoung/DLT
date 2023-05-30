package com.dopamines.backend.review.repository;

import com.dopamines.backend.plan.entity.Plan;
import com.dopamines.backend.review.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Optional<Photo> findByPlan(Plan plan);

    List<Photo> findAllByPlan(Plan plan);

}
