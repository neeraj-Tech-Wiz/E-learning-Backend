package com.elearning.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tests")

public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @NotNull(message = "Standard is required")
    @Column(nullable = false)
    private Integer standard;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
//    @JsonManagedReference
    private List<Question> questions;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean reviewEnabled = false;

    @Column(nullable = false, columnDefinition = "integer default 30")
    private Integer durationMinutes = 30;

    public Test() {}

    public Test(String title, LocalDate date, Teacher teacher) {
        this.title = title;
        this.date = date;
        this.teacher = teacher;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public Integer getStandard() { return standard;}

    public void setStandard(Integer standard) { this.standard = standard; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public boolean isReviewEnabled() {
        return reviewEnabled;
    }

    public void setReviewEnabled(boolean reviewEnabled) {
        this.reviewEnabled = reviewEnabled;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}