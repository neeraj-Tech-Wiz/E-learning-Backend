package com.elearning.backend.service;

import com.elearning.backend.dto.AssignmentGradeResultDTO;
import com.elearning.backend.dto.AssignmentDTO;
import com.elearning.backend.model.*;
import com.elearning.backend.repository.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Autowired private AssignmentRepository     assignmentRepository;
    @Autowired private AssignmentSubmissionRepository submissionRepository;
    @Autowired private TeacherRepository        teacherRepository;
    @Autowired private StudentRepository        studentRepository;

    private final RestTemplate  restTemplate  = new RestTemplate();
    private final ObjectMapper  objectMapper  = new ObjectMapper();

    /* ══════════════════════════════════════════════════════
       TEACHER: create an assignment with a rubric
    ══════════════════════════════════════════════════════ */
    public AssignmentDTO createAssignment(
            String title, String rubric, Integer totalMarks,
            Integer standard, String subject, String teacherEmail
    ) {
        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Assignment a = new Assignment();
        a.setTitle(title);
        a.setRubric(rubric);
        a.setTotalMarks(totalMarks);
        a.setStandard(standard);
        a.setSubject(subject);
        a.setTeacher(teacher);
        a.setCreatedAt(LocalDateTime.now());
        a.setActive(true);

        return toDTO(assignmentRepository.save(a));
    }

    /* ══════════════════════════════════════════════════════
       TEACHER: list their assignments
    ══════════════════════════════════════════════════════ */
    public List<AssignmentDTO> getTeacherAssignments(String teacherEmail) {
        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return assignmentRepository
                .findByTeacherOrderByCreatedAtDesc(teacher)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /* ══════════════════════════════════════════════════════
       STUDENT: get active assignments for their standard
    ══════════════════════════════════════════════════════ */
    public List<AssignmentDTO> getStudentAssignments(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return assignmentRepository
                .findByStandardAndActiveTrueOrderByCreatedAtDesc(student.getStandard())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /* ══════════════════════════════════════════════════════
       STUDENT: submit PDF → extract text → Gemini grades it
    ══════════════════════════════════════════════════════ */
    public AssignmentGradeResultDTO submitAndGrade(
            Long assignmentId, MultipartFile pdfFile, String studentEmail
    ) throws Exception {

//        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        /* ── Step 1: Extract text from uploaded PDF ── */
        String extractedText = extractTextFromPdf(pdfFile);

        if (extractedText == null || extractedText.isBlank()) {
            throw new RuntimeException(
                    "Could not extract text from PDF. " +
                            "Make sure the PDF contains selectable text (not just a scanned image)."
            );
        }

        /* ── Step 2: Save submission as PENDING ── */
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setExtractedText(extractedText);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus("PENDING");
        submission = submissionRepository.save(submission);

        /* ── Step 3: Call Gemini to grade ── */
        try {
            AssignmentGradeResultDTO result = callGeminiForGrading(
                    extractedText, assignment.getRubric(), assignment.getTotalMarks(),
                    assignment.getTitle(), assignment.getSubject()
            );

            /* ── Step 4: Save grade results ── */
            submission.setMarksAwarded(result.getMarksAwarded());
            submission.setRemarks(result.getRemarks());
            submission.setImprovementTips(result.getImprovementTips());
            submission.setGradedAt(LocalDateTime.now());
            submission.setStatus("GRADED");
            submissionRepository.save(submission);

            result.setSubmissionId(submission.getId());
            result.setAssignmentTitle(assignment.getTitle());
            result.setTotalMarks(assignment.getTotalMarks());
            return result;

        } catch (Exception e) {

            e.printStackTrace();

            submission.setStatus("FAILED");
            submission.setRemarks("Gemini Error: " + e.getMessage());

            submissionRepository.save(submission);

            throw e;
        }
    }

    /* ══════════════════════════════════════════════════════
       STUDENT: get their submission results
    ══════════════════════════════════════════════════════ */
    public List<AssignmentGradeResultDTO> getStudentSubmissions(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return submissionRepository.findByStudentOrderBySubmittedAtDesc(student)
                .stream()
                .map(s -> {
                    AssignmentGradeResultDTO dto = new AssignmentGradeResultDTO();
                    dto.setSubmissionId(s.getId());
                    dto.setAssignmentTitle(s.getAssignment().getTitle());
                    dto.setTotalMarks(s.getAssignment().getTotalMarks());
                    dto.setMarksAwarded(s.getMarksAwarded());
                    dto.setRemarks(s.getRemarks());
                    dto.setImprovementTips(s.getImprovementTips());
                    dto.setStatus(s.getStatus());
                    dto.setSubmittedAt(s.getSubmittedAt() != null ? s.getSubmittedAt().toString() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /* ══════════════════════════════════════════════════════
       TEACHER: view all submissions for an assignment
    ══════════════════════════════════════════════════════ */
    public List<AssignmentGradeResultDTO> getSubmissionsForAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentIdOrderBySubmittedAtDesc(assignmentId)
                .stream()
                .map(s -> {
                    AssignmentGradeResultDTO dto = new AssignmentGradeResultDTO();
                    dto.setSubmissionId(s.getId());
                    dto.setAssignmentTitle(s.getAssignment().getTitle());
                    dto.setTotalMarks(s.getAssignment().getTotalMarks());
                    dto.setMarksAwarded(s.getMarksAwarded());
                    dto.setRemarks(s.getRemarks());
                    dto.setImprovementTips(s.getImprovementTips());
                    dto.setStatus(s.getStatus());
                    dto.setStudentName(s.getStudent().getName());
                    dto.setSubmittedAt(s.getSubmittedAt() != null ? s.getSubmittedAt().toString() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /* ══════════════════════════════════════════════════════
       PRIVATE: Extract text from PDF using Apache PDFBox
    ══════════════════════════════════════════════════════ */
    private String extractTextFromPdf(MultipartFile file) throws Exception {
        try (PDDocument doc = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc).trim();
        }
    }

    /* ══════════════════════════════════════════════════════
       PRIVATE: Call Gemini API for grading
    ══════════════════════════════════════════════════════ */
    private AssignmentGradeResultDTO callGeminiForGrading(
            String studentAnswer, String rubric,
            int totalMarks, String assignmentTitle, String subject
    ) throws Exception {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        String prompt = String.format("""
            You are an expert teacher grading a student's assignment.
            
            Assignment: %s
            Subject: %s
            Total Marks: %d
            
            RUBRIC / MARKING CRITERIA:
            %s
            
            STUDENT'S ANSWER:
            %s
            
            Grade the student's answer strictly based on the rubric above.
            Respond ONLY with valid JSON in this exact format (no markdown, no extra text):
            {
              "marksAwarded": <integer between 0 and %d>,
              "remarks": "<2-3 sentence assessment of the answer quality>",
              "improvementTips": "<2-3 specific, actionable tips the student can use to improve>"
            }
            """,
                assignmentTitle, subject, totalMarks,
                rubric, studentAnswer, totalMarks
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> part    = Map.of("text", prompt);
        Map<String, Object> content = Map.of("role", "user", "parts", List.of(part));
        Map<String, Object> body    = Map.of("contents", List.of(content));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url, request, String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Gemini API error: " + response.getStatusCode());
        }

        /* Parse Gemini response */
        JsonNode root        = objectMapper.readTree(response.getBody());
        String   rawText     = root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();

        /* Strip markdown fences if Gemini wraps in ```json ``` */
        String cleanJson = rawText
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();

        JsonNode result = objectMapper.readTree(cleanJson);

        AssignmentGradeResultDTO dto = new AssignmentGradeResultDTO();
        dto.setMarksAwarded(result.path("marksAwarded").asInt());
        dto.setRemarks(result.path("remarks").asText());
        dto.setImprovementTips(result.path("improvementTips").asText());
        dto.setStatus("GRADED");
        return dto;
    }

    /* ── Entity → DTO ── */
    private AssignmentDTO toDTO(Assignment a) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(a.getId());
        dto.setTitle(a.getTitle());
        dto.setRubric(a.getRubric());
        dto.setTotalMarks(a.getTotalMarks());
        dto.setStandard(a.getStandard());
        dto.setSubject(a.getSubject());
        dto.setTeacherName(a.getTeacher().getName());
        dto.setCreatedAt(a.getCreatedAt().toString());
        return dto;
    }
}