package com.elearning.backend.dto;
import lombok.Data;
import java.util.List;
@Data
public class TestReviewResponse {
    private Long testId;
    private int score;
    private int total;
    private boolean reviewEnabled;
    private List<QuestionReviewDTO> questions;

}

