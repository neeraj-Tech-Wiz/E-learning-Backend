package com.elearning.backend.controller;

import com.elearning.backend.dto.StudyMaterialDTO;
import com.elearning.backend.model.StudyMaterial;
import com.elearning.backend.service.StudyMaterialMapper;
import com.elearning.backend.service.StudyMaterialService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/materials")
//@CrossOrigin("*")
public class StudyMaterialController {

    private final StudyMaterialService studyMaterialService;
    private final StudyMaterialMapper materialMapper;

    public StudyMaterialController(StudyMaterialService service,
                                   StudyMaterialMapper materialMapper) {
        this.studyMaterialService = service;
        this.materialMapper = materialMapper;
    }

    // 1. UPLOAD ENDPOINT
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )    @PreAuthorize("hasRole('ROLE_TEACHER')") // <-- Only teachers can access
    public ResponseEntity<StudyMaterialDTO> uploadMaterial(
            Principal principal, // <-- Get the identity (email) from the token
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file,
            @RequestParam("targetStandard") int targetStandard,
            @RequestParam("subject") String subject
    ) throws IOException {

        StudyMaterial savedEntity = studyMaterialService.uploadMaterial(
                principal.getName(), title, file, targetStandard, subject
        );

        StudyMaterialDTO dto = materialMapper.toDto(savedEntity);

        // 3. Return the DTO with the 201 CREATED status
        return ResponseEntity
                .status(HttpStatus.CREATED) // Sets the HTTP Status Code to 201
                .body(dto);    }

    @GetMapping("/secure/download/{materialId}")
    public ResponseEntity<Void> secureDownloadMaterial(
            Principal principal,
            @PathVariable Long materialId
    ) throws AccessDeniedException {

        String url = studyMaterialService.downloadMaterialSecurely(
                principal.getName(),
                materialId
        );

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }

    @GetMapping("/student/content") // <-- FIX 1: SIMPLIFIED URL PATH
    public ResponseEntity<List<StudyMaterialDTO>> getStudentMaterials(
            Principal principal, // <-- FIX 2: GETS USER EMAIL FROM JWT TOKEN
            @RequestParam(required = false, defaultValue = "") String subject
    ) {
        // Delegate secure filtering, using the email (username) from the token
        List<StudyMaterialDTO> dtos = studyMaterialService.getMaterialsForStudent(
                principal.getName(), // principal.getName() is the user's email/username
                subject
        );
        return ResponseEntity.ok(dtos);
    }

    // 2. DOWNLOAD ENDPOINT
    @GetMapping("/download/{materialId}")
    public ResponseEntity<Void> downloadLecture(
            @PathVariable Long materialId
    ) {

        String url = studyMaterialService.downloadMaterialUnsecured(materialId);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }

    // 3. GET BY TEACHER ENDPOINT
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<StudyMaterial>> getMaterialsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(studyMaterialService.getMaterialsByTeacher(teacherId));
    }
}