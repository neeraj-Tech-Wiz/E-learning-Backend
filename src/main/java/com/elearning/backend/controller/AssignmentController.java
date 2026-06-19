package com.elearning.backend.controller;

import com.elearning.backend.dto.AssignmentDTO;
import com.elearning.backend.dto.AssignmentGradeResultDTO;
import com.elearning.backend.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    private boolean hasRole(Authentication auth, String role) {
        if (auth == null) return false;
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(role));
    }

    /* ── TEACHER: create assignment ── */
    @PostMapping
    public ResponseEntity<?> createAssignment(
            @RequestBody CreateAssignmentRequest req,
            Authentication auth
    ) {
        if (!hasRole(auth, "ROLE_TEACHER"))
            return ResponseEntity.status(403).body("ROLE_TEACHER required");

        AssignmentDTO result = assignmentService.createAssignment(
                req.getTitle(), req.getRubric(), req.getTotalMarks(),
                req.getStandard(), req.getSubject(), auth.getName()
        );
        return ResponseEntity.ok(result);
    }

    /* ── TEACHER: list their assignments ── */
    @GetMapping("/mine")
    public ResponseEntity<?> myAssignments(Authentication auth) {
        if (!hasRole(auth, "ROLE_TEACHER"))
            return ResponseEntity.status(403).body("ROLE_TEACHER required");
        return ResponseEntity.ok(assignmentService.getTeacherAssignments(auth.getName()));
    }

    /* ── TEACHER: view submissions for an assignment ── */
    @GetMapping("/{assignmentId}/submissions")
    public ResponseEntity<?> submissionsForAssignment(
            @PathVariable Long assignmentId,
            Authentication auth
    ) {
        if (!hasRole(auth, "ROLE_TEACHER"))
            return ResponseEntity.status(403).body("ROLE_TEACHER required");
        return ResponseEntity.ok(assignmentService.getSubmissionsForAssignment(assignmentId));
    }

    /* ── STUDENT: get active assignments for their standard ── */
    @GetMapping("/student")
    public ResponseEntity<?> studentAssignments(Authentication auth) {
        if (!hasRole(auth, "ROLE_STUDENT"))
            return ResponseEntity.status(403).body("ROLE_STUDENT required");
        return ResponseEntity.ok(assignmentService.getStudentAssignments(auth.getName()));
    }

    /* ── STUDENT: submit PDF and get AI grade ── */
    @PostMapping("/{assignmentId}/submit")
    public ResponseEntity<?> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file,
            Authentication auth
    ) {
        if (!hasRole(auth, "ROLE_STUDENT"))
            return ResponseEntity.status(403).body("ROLE_STUDENT required");

        if (file.isEmpty())
            return ResponseEntity.badRequest().body("Please upload a PDF file");

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf"))
            return ResponseEntity.badRequest().body("Only PDF files are accepted");

        try {
            AssignmentGradeResultDTO result = assignmentService.submitAndGrade(
                    assignmentId, file, auth.getName()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Grading failed: " + e.getMessage());
        }
    }

    /* ── STUDENT: view their past submissions ── */
    @GetMapping("/student/submissions")
    public ResponseEntity<?> mySubmissions(Authentication auth) {
        if (!hasRole(auth, "ROLE_STUDENT"))
            return ResponseEntity.status(403).body("ROLE_STUDENT required");
        return ResponseEntity.ok(assignmentService.getStudentSubmissions(auth.getName()));
    }

    /* ── Request body ── */
    public static class CreateAssignmentRequest {
        private String  title;
        private String  rubric;
        private Integer totalMarks;
        private Integer standard;
        private String  subject;

        public String  getTitle()                  { return title; }
        public void    setTitle(String t)           { this.title = t; }
        public String  getRubric()                 { return rubric; }
        public void    setRubric(String r)          { this.rubric = r; }
        public Integer getTotalMarks()             { return totalMarks; }
        public void    setTotalMarks(Integer m)     { this.totalMarks = m; }
        public Integer getStandard()               { return standard; }
        public void    setStandard(Integer s)       { this.standard = s; }
        public String  getSubject()                { return subject; }
        public void    setSubject(String s)         { this.subject = s; }
    }
}