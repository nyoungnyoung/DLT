package com.dopamines.backend.test.repository;

import com.dopamines.backend.test.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestRepository  extends JpaRepository<Test, Long> {
    //커스텀 쿼리 메서드
    List<Test> findByNameContaining(String name);
}