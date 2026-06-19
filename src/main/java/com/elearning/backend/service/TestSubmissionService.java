package com.elearning.backend.service;

import com.elearning.backend.dto.StudentSubmissionDTO;
import com.elearning.backend.dto.StudentResultDTO;
import com.elearning.backend.dto.StudentSubmissionSummaryDTO;
import com.elearning.backend.model.Question;
import com.elearning.backend.model.Student;
import com.elearning.backend.model.StudentAnswer;
import com.elearning.backend.model.StudentTestResult;
import com.elearning.backend.model.Test;
import com.elearning.backend.repository.QuestionRepository;
import com.elearning.backend.repository.StudentAnswerRepository;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.StudentTestResultRepository;
import com.elearning.backend.repository.TestRepository;
import com.elearning.backend.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TestSubmissionService {

    private final StudentRepository studentRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final StudentTestResultRepository resultRepository;
    private final StudentAnswerRepository answerRepository;

    public TestSubmissionService(StudentRepository studentRepository,
                                 TestRepository testRepository,
                                 QuestionRepository questionRepository,
                                 StudentTestResultRepository resultRepository,
                                 StudentAnswerRepository answerRepository) {
        this.studentRepository = studentRepository;
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
        this.answerRepository = answerRepository;
    }

    public Test fetchTestForStudent(Long testId, String studentEmail) throws AccessDeniedException {

        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Test test = testRepository.findByIdWithQuestions(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found"));

        if (!Objects.equals(test.getStandard(), student.getStandard())) {
            throw new AccessDeniedException("Test not available for your standard");
        }

        return test;
    }


    @Transactional
    public StudentResultDTO submitAnswers(String studentEmail, Long testId, StudentSubmissionDTO submission) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentEmail));

        Test test = testRepository.findByIdWithQuestions(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found: " + testId));

        Optional<StudentTestResult> existing = resultRepository.findByStudentAndTest(student, test);
        if (existing.isPresent()) {
            throw new IllegalStateException("Student has already attempted this test.");
        }

        Map<Long, Question> questionMap = new HashMap<>();
        for (Question q : test.getQuestions()) {
            questionMap.put(q.getId(), q);
        }

        int correctCount = 0;
        int total = test.getQuestions() == null ? 0 : test.getQuestions().size();

        StudentTestResult result = new StudentTestResult();
        result.setStudent(student);
        result.setTest(test);
        result.setDateTaken(LocalDateTime.now());
        result.setTotalQuestions(total);
        result = resultRepository.save(result);

        List<StudentAnswer> savedAnswers = new ArrayList<>();
        List<StudentResultDTO.QuestionResult> qResults = new ArrayList<>();

        for (StudentSubmissionDTO.AnswerDTO a : submission.getAnswers()) {
            Question q = questionMap.get(a.getQuestionId());
            if (q == null) continue;

            String submitted = a.getSelectedAnswer();
            boolean isCorrect = submitted != null && submitted.equalsIgnoreCase(q.getCorrectAnswer());
            if (isCorrect) correctCount++;

            StudentAnswer sa = new StudentAnswer();
            sa.setResult(result);
            sa.setQuestion(q);
            sa.setSubmittedAnswer(submitted);
            savedAnswers.add(sa);

            StudentResultDTO.QuestionResult qr = new StudentResultDTO.QuestionResult();
            qr.setQuestionId(q.getId());
            qr.setQuestionIndex(q.getQuestionIndex());
            qr.setQuestionText(q.getQuestionText());
            qr.setSelectedAnswer(submitted);
            qr.setCorrectAnswer(q.getCorrectAnswer());
            qr.setCorrect(isCorrect);
            qResults.add(qr);
        }

        answerRepository.saveAll(savedAnswers);

        result.setScore(correctCount);
        result.setTotalQuestions(total);
        result.setDateTaken(LocalDateTime.now());
        resultRepository.save(result);

        StudentResultDTO dto = new StudentResultDTO();
        dto.setResultId(result.getId());
        dto.setTestId(test.getId());
        dto.setScore(correctCount);
        dto.setTotalQuestions(total);
        dto.setDateTaken(result.getDateTaken());
        dto.setQuestionResults(qResults);

        return dto;
    }

    public Optional<StudentTestResult> getStudentResult(String studentEmail, Long testId) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentEmail));
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found: " + testId));
        return resultRepository.findByStudentAndTest(student, test);
    }

    public List<StudentSubmissionSummaryDTO> getAllResultsForTest(Long testId) {
        List<StudentTestResult> results = resultRepository.findByTestId(testId);
        List<StudentSubmissionSummaryDTO> dto = new ArrayList<>();

        for (StudentTestResult r : results) {
            StudentSubmissionSummaryDTO d = new StudentSubmissionSummaryDTO();
            d.setId(r.getId());

            if (r.getStudent() != null) {
                d.setStudentName(r.getStudent().getName());
                d.setStudentEmail(r.getStudent().getEmail());
            } else {
                d.setStudentName("N/A");
                d.setStudentEmail("N/A");
            }
            d.setScore(r.getScore());
            d.setTotalQuestions(r.getTotalQuestions());
            d.setDateTaken(r.getDateTaken().toString());
            dto.add(d);
        }
        return dto;
    }
}
