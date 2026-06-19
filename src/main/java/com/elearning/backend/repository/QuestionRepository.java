package com.elearning.backend.repository;

import com.elearning.backend.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT MAX(q.questionIndex) FROM Question q WHERE q.test.id = :testId")
    Integer findMaxQuestionIndexByTestId(Long testId);
}
