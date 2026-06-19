package com.elearning.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class StudentSubmissionDTO {
    // list of answers
    private List<AnswerDTO> answers;

    @Data
    public static class AnswerDTO {
        private Long questionId;
        private String selectedAnswer; // "A","B","C","D" now
    }
}
