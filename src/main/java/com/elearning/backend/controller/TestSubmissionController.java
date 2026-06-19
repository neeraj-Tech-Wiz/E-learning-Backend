package com.elearning.backend.controller;

import com.elearning.backend.dto.*;
import com.elearning.backend.model.Question;
import com.elearning.backend.model.StudentAnswer;
import com.elearning.backend.model.StudentTestResult;
import com.elearning.backend.model.Test;
import com.elearning.backend.service.StudentAnswerService;
import com.elearning.backend.service.TestService;
import com.elearning.backend.service.TestSubmissionService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tests")
@CrossOrigin(origins = "*")
public class TestSubmissionController {

    private final TestSubmissionService submissionService;
    private final StudentAnswerService answerService;
    private final TestService testService;

    public TestSubmissionController(TestSubmissionService submissionService,
                                    StudentAnswerService answerService,
                                    TestService testService) {
        this.submissionService = submissionService;
        this.answerService = answerService;
        this.testService = testService;
    }

    // STUDENT: START TEST
    @GetMapping("/{id}/start")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> startTest(@PathVariable Long id, Principal principal) throws Exception {

        Test test = submissionService.fetchTestForStudent(id, principal.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("testId", test.getId());
        response.put("title", test.getTitle());
        response.put("durationMinutes", test.getDurationMinutes());

        List<StudentQuestionDTO> dto = test.getQuestions().stream()
                .sorted(Comparator.comparingInt(Question::getQuestionIndex))
                .map(this::toStudentQuestionDto)
                .collect(Collectors.toList());

        response.put("questions", dto);

        return ResponseEntity.ok(response);
    }

    // STUDENT: SUBMIT TEST
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<StudentResultDTO> submitTest(
            @PathVariable Long id,
            @RequestBody StudentSubmissionDTO submission,
            Principal principal) {

        String studentEmail = principal.getName();
        StudentResultDTO result = submissionService.submitAnswers(studentEmail, id, submission);

        return ResponseEntity.ok(result);
    }

    // STUDENT: VIEW RESULT (score only or full review)
    @GetMapping("/{id}/result")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> getResult(@PathVariable Long id, Principal principal) {

        String studentEmail = principal.getName();

        Optional<StudentTestResult> opt = submissionService.getStudentResult(studentEmail, id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        StudentTestResult result = opt.get();
        Test test = result.getTest();
// --------------------------------------------------
// STUDENT RESULT / REVIEW RESPONSE
// --------------------------------------------------
        if (!test.isReviewEnabled()) {
            Map<String, Object> response = new HashMap<>();
            response.put("testId", id);
            response.put("score", result.getScore());
            response.put("totalQuestions", result.getTotalQuestions());
            response.put("reviewEnabled", false);
            response.put("dateTaken", result.getDateTaken());
            return ResponseEntity.ok(response);
        }

        TestReviewResponse review = new TestReviewResponse();
        review.setTestId(id);
        review.setScore(result.getScore());
        review.setTotal(result.getTotalQuestions());
        review.setReviewEnabled(true);

        List<Question> allQuestions = test.getQuestions();

        Map<Long, StudentAnswer> answerMap =
                answerService.findByStudentAndTest(
                        result.getStudent().getId(),
                        id
                ).stream().collect(Collectors.toMap(
                        a -> a.getQuestion().getId(),
                        a -> a
                ));

        List<QuestionReviewDTO> questionReviews = allQuestions.stream()
                .sorted(Comparator.comparingInt(Question::getQuestionIndex))
                .map(q -> {
                    StudentAnswer ans = answerMap.get(q.getId());

                    QuestionReviewDTO dto = new QuestionReviewDTO();
                    dto.setId(q.getId());
                    dto.setQuestionText(q.getQuestionText());
                    dto.setOptionA(q.getOptionA());
                    dto.setOptionB(q.getOptionB());
                    dto.setOptionC(q.getOptionC());
                    dto.setOptionD(q.getOptionD());
                    dto.setCorrectAnswer(q.getCorrectAnswer());

                    if (ans != null) {
                        // Answered
                        dto.setStudentAnswer(ans.getSubmittedAnswer());
                        dto.setCorrect(
                                q.getCorrectAnswer()
                                        .equals(ans.getSubmittedAnswer())
                        );
                    } else {
                        dto.setStudentAnswer(null);
                        dto.setCorrect(false);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        review.setQuestions(questionReviews);

        return ResponseEntity.ok(review);

    }

    // TEACHER: VIEW ALL SUBMISSIONS (summary DTO)
    @GetMapping("/{id}/submissions")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<List<StudentSubmissionSummaryDTO>> getAllSubmissions(@PathVariable Long id) {

        // Service returns FULLY READY DTOs
        List<StudentSubmissionSummaryDTO> results = submissionService.getAllResultsForTest(id);

        return ResponseEntity.ok(results);
    }

    
    // TEACHER: ENABLE REVIEW
    @PatchMapping("/{id}/enable-review")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<?> enableReview(@PathVariable Long id) {
        Test test = testService.getTestById(id);
        test.setReviewEnabled(true);
        testService.save(test);

        Map<String, Object> resp = new HashMap<>();
        resp.put("testId", id);
        resp.put("reviewEnabled", true);
        return ResponseEntity.ok(resp);
    }

    // TEACHER: DISABLE REVIEW
    @PatchMapping("/{id}/disable-review")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<?> disableReview(@PathVariable Long id) {
        Test test = testService.getTestById(id);
        test.setReviewEnabled(false);
        testService.save(test);

        Map<String, Object> resp = new HashMap<>();
        resp.put("testId", id);
        resp.put("reviewEnabled", false);
        return ResponseEntity.ok(resp);
    }

    private StudentQuestionDTO toStudentQuestionDto(Question q) {
        StudentQuestionDTO d = new StudentQuestionDTO();
        d.setId(q.getId());
        d.setQuestionIndex(q.getQuestionIndex());
        d.setQuestionText(q.getQuestionText());
        d.setOptionA(q.getOptionA());
        d.setOptionB(q.getOptionB());
        d.setOptionC(q.getOptionC());
        d.setOptionD(q.getOptionD());
        return d;
    }
}
