package com.elearning.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    /* The rubric / marking criteria the teacher provides.
       Gemini grades the student's submission against this. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String rubric;

    /* Total marks this assignment is worth */
    @Column(nullable = false)
    private Integer totalMarks;

    /* Which standard this is for */
    @Column(nullable = false)
    private Integer standard;

    @Column(nullable = false)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean active = true;

    // ── Getters & Setters ──
    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }
    public String getTitle()                   { return title; }
    public void setTitle(String t)             { this.title = t; }
    public String getRubric()                  { return rubric; }
    public void setRubric(String r)            { this.rubric = r; }
    public Integer getTotalMarks()             { return totalMarks; }
    public void setTotalMarks(Integer m)       { this.totalMarks = m; }
    public Integer getStandard()               { return standard; }
    public void setStandard(Integer s)         { this.standard = s; }
    public String getSubject()                 { return subject; }
    public void setSubject(String s)           { this.subject = s; }
    public Teacher getTeacher()                { return teacher; }
    public void setTeacher(Teacher t)          { this.teacher = t; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime t)  { this.createdAt = t; }
    public boolean isActive()                 { return active; }
    public void setActive(boolean a)          { this.active = a; }
}