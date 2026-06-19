package com.elearning.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StudentResultDTO {
    private Long resultId;
    private Long testId;
    private int score;
    private int totalQuestions;
    private LocalDateTime dateTaken;
    private List<QuestionResult> questionResults;

    @Data
    public static class QuestionResult {
        private Long questionId;
        private int questionIndex;
        private String questionText;
        private String selectedAnswer;
        private String correctAnswer;
        private boolean correct;
    }//now
}
