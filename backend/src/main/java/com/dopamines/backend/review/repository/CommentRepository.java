package com.dopamines.backend.review.repository;

import com.dopamines.backend.plan.entity.Plan;
import com.dopamines.backend.review.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findById(Long commentId);
    List<Comment> findByPlan(Plan plan);
}
