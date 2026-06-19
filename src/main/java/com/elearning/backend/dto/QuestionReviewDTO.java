package com.elearning.backend.dto;
import lombok.Data;
@Data
public class QuestionReviewDTO {
    private Long id;
    private String questionText;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private String correctAnswer;
    private String studentAnswer;
    private boolean isCorrect;
}
