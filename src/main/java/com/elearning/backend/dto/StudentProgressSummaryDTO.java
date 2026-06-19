package com.elearning.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class StudentProgressSummaryDTO {

    private Long studentId;
    private String studentName;
    private Integer studentStandard;

    private Long totalLectures;
    private Long completedLectures;

    private Long totalMaterials;
    private Long completedMaterials;

    private Double overallPercentage;

    private List<ProgressStatusDTO> progressList;
}
