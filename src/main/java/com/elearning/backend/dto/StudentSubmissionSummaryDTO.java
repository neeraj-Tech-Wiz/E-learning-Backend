package com.elearning.backend.dto;

import lombok.Data;

@Data
public class StudentSubmissionSummaryDTO {
    private Long id;
    private String studentName;
    private String studentEmail;
    private int score;
    private int totalQuestions;
    private String dateTaken;
}
