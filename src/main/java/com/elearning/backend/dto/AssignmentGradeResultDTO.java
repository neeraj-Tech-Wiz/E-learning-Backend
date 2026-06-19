package com.elearning.backend.dto;
public class AssignmentGradeResultDTO {
    private Long    submissionId;
    private String  assignmentTitle;
    private Integer totalMarks;
    private Integer marksAwarded;
    private String  remarks;
    private String  improvementTips;
    private String  status;           // PENDING / GRADED / FAILED
    private String  studentName;      // for teacher view
    private String  submittedAt;

    // standard getters/setters for all fields


    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public String getAssignmentTitle() {
        return assignmentTitle;
    }

    public void setAssignmentTitle(String assignmentTitle) {
        this.assignmentTitle = assignmentTitle;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Integer getMarksAwarded() {
        return marksAwarded;
    }

    public void setMarksAwarded(Integer marksAwarded) {
        this.marksAwarded = marksAwarded;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getImprovementTips() {
        return improvementTips;
    }

    public void setImprovementTips(String improvementTips) {
        this.improvementTips = improvementTips;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }
}
