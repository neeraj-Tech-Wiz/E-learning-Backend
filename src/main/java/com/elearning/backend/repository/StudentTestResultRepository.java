package com.elearning.backend.repository;

import com.elearning.backend.model.StudentTestResult;
import com.elearning.backend.model.Student;
import com.elearning.backend.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface StudentTestResultRepository extends JpaRepository<StudentTestResult, Long> {
    Optional<StudentTestResult> findByStudentAndTest(Student student, Test test);
    List<StudentTestResult> findByTestId(Long testId);

    @Query("""
            SELECT r FROM StudentTestResult r
            JOIN FETCH r.test t
            WHERE r.student.id = :studentId
            """)
    List<StudentTestResult> findAllByStudentIdWithTest(Long studentId);

}
