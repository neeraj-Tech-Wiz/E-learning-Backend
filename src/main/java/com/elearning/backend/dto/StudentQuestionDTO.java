package com.elearning.backend.dto;

import lombok.Data;

@Data
public class StudentQuestionDTO {
    private Long id;
    private int questionIndex;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
}
