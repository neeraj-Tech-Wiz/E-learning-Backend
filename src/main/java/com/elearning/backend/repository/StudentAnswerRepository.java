package com.elearning.backend.repository;

import com.elearning.backend.model.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {

    List<StudentAnswer> findByResultId(Long resultId);

    List<StudentAnswer> findByResult_Student_IdAndResult_Test_Id(Long studentId, Long testId);
}
