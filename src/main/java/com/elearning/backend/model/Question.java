package com.elearning.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor; // Required for constructor with all arguments
import lombok.Data;              // Provides Getters, Setters, toString, hashCode, and equals
import lombok.NoArgsConstructor;   // Provides the default constructor (required by JPA/Jackson)
import lombok.experimental.Accessors; // Optional, can be used for chaining setters

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "integer default 0")
    private int questionIndex;

    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;

    @ManyToOne
    @JoinColumn(name = "test_id")
    @JsonIgnore
    private Test test;

}