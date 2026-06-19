package com.elearning.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_submissions")
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /* Text extracted from the student's uploaded PDF */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String extractedText;

    /* AI-generated marks out of assignment.totalMarks */
    @Column
    private Integer marksAwarded;

    /* AI-generated feedback/remarks */
    @Column(columnDefinition = "TEXT")
    private String remarks;

    /* AI-generated tips for improvement */
    @Column(columnDefinition = "TEXT")
    private String improvementTips;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    /* PENDING → GRADED → FAILED */
    @Column(nullable = false)
    private String status = "PENDING";

    // ── Getters & Setters ──
    public Long getId()                           { return id; }
    public void setId(Long id)                    { this.id = id; }
    public Assignment getAssignment()             { return assignment; }
    public void setAssignment(Assignment a)       { this.assignment = a; }
    public Student getStudent()                   { return student; }
    public void setStudent(Student s)             { this.student = s; }
    public String getExtractedText()              { return extractedText; }
    public void setExtractedText(String t)        { this.extractedText = t; }
    public Integer getMarksAwarded()              { return marksAwarded; }
    public void setMarksAwarded(Integer m)        { this.marksAwarded = m; }
    public String getRemarks()                    { return remarks; }
    public void setRemarks(String r)              { this.remarks = r; }
    public String getImprovementTips()            { return improvementTips; }
    public void setImprovementTips(String t)      { this.improvementTips = t; }
    public LocalDateTime getSubmittedAt()         { return submittedAt; }
    public void setSubmittedAt(LocalDateTime t)   { this.submittedAt = t; }
    public LocalDateTime getGradedAt()            { return gradedAt; }
    public void setGradedAt(LocalDateTime t)      { this.gradedAt = t; }
    public String getStatus()                     { return status; }
    public void setStatus(String s)               { this.status = s; }
}