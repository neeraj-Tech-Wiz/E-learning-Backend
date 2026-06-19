package com.elearning.backend.service;

import com.elearning.backend.model.StudentAnswer;
import com.elearning.backend.repository.StudentAnswerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentAnswerService {

    private final StudentAnswerRepository studentAnswerRepository;

    // Constructor injection
    public StudentAnswerService(StudentAnswerRepository studentAnswerRepository) {
        this.studentAnswerRepository = studentAnswerRepository;
    }

    /**
     * Fetch answers for a given student & test.
     */
    public List<StudentAnswer> findByStudentAndTest(Long studentId, Long testId) {
        return studentAnswerRepository.findByResult_Student_IdAndResult_Test_Id(studentId, testId);
    }

}
