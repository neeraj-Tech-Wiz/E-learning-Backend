package com.elearning.backend.repository;

import com.elearning.backend.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByTeacherId(Long teacherId);

    @Query("SELECT t FROM Test t LEFT JOIN FETCH t.questions q WHERE t.id = :id")
    Optional<Test> findByIdWithQuestions(@Param("id") Long id);

    List<Test> findByStandard(int standard);
}
