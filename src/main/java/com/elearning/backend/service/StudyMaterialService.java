package com.elearning.backend.service;

import com.elearning.backend.dto.StudyMaterialDTO;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Student;
import com.elearning.backend.model.StudentProgress;
import com.elearning.backend.model.StudyMaterial;
import com.elearning.backend.repository.StudentProgressRepository;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.StudyMaterialRepository;
import com.elearning.backend.model.Teacher; // Required for fetching Teacher during upload
import com.elearning.backend.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.elearning.backend.dto.CloudinaryUploadResult;
@Service
@RequiredArgsConstructor
public class StudyMaterialService {

    private final Path rootDir = Paths.get("uploads/study_materials").toAbsolutePath().normalize();

    // Injected Dependencies
    private final StudyMaterialRepository materialRepository;
    private final StudentRepository studentRepository;
    private final StudyMaterialMapper materialMapper;
    private final TeacherRepository teacherRepository; // Assuming you need this for upload
    private final StudentProgressRepository studentProgressRepository;
    private final CloudinaryService cloudinaryService;

    // =========================================================================
    // UPLOAD LOGIC (Set Standard & Subject)
    // =========================================================================

    public StudyMaterial uploadMaterial(
            String teacherEmail,
            String title,
            MultipartFile file,
            int targetStandard,
            String subject
    ) throws IOException {

        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Teacher not found: " + teacherEmail
                        ));

        // Upload PDF/DOC/DOCX/etc to Cloudinary
        CloudinaryUploadResult uploadResult =
                cloudinaryService.uploadPdf(file);

        String originalFilename = file.getOriginalFilename();

        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension =
                    originalFilename.substring(
                            originalFilename.lastIndexOf('.') + 1
                    );
        }

        StudyMaterial material = new StudyMaterial();

        material.setTitle(title);
        material.setFileType(extension);

        // IMPORTANT
        material.setFilePath(uploadResult.getUrl());

        material.setUploadDate(LocalDateTime.now());
        material.setTeacher(teacher);
        material.setTargetStandard(targetStandard);
        material.setSubject(subject);

        return materialRepository.save(material);
    }
    // =========================================================================
    // GET FOR STUDENT (SECURE FILTERING LOGIC)
    // =========================================================================

public List<StudyMaterialDTO> getMaterialsForStudent(String studentEmail, String searchSubject) {

    // 1️⃣ Fetch the Student entity securely
    Student student = studentRepository.findByEmail(studentEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found for email: " + studentEmail));

    Integer studentStandard = student.getStandard();
    Long studentId = student.getId();

    // 2️⃣ Fetch all study materials matching student’s standard and subject filter
    List<StudyMaterial> entities = materialRepository
            .findByTargetStandardAndSubjectContainingIgnoreCase(
                    studentStandard,
                    searchSubject
            );

    // 3️⃣ Fetch all completed materials for this student from progress table
    List<StudentProgress> completedProgress = studentProgressRepository
            .findByStudentIdAndContentTypeAndIsCompletedTrue(studentId, "MATERIAL");

    Set<Long> completedIds = completedProgress.stream()
            .map(StudentProgress::getContentId)
            .collect(Collectors.toSet());

    // 4️⃣ Map to DTO and inject completion status
    return entities.stream()
            .map(material -> {
                StudyMaterialDTO dto = materialMapper.toDto(material);
                dto.setCompleted(completedIds.contains(material.getId())); // 👈 add completion info
                return dto;
            })
            .collect(Collectors.toList());
}


    // =========================================================================
    // DOWNLOAD LOGIC (SECURE)
    // =========================================================================

    public String downloadMaterialSecurely(String studentEmail, Long materialId)
            throws AccessDeniedException {

        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found for email: " + studentEmail));

        StudyMaterial material = materialRepository.findById(materialId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Study Material not found"));

        if (student.getStandard() != material.getTargetStandard()) {
            throw new AccessDeniedException(
                    "Access denied."
            );
        }

        return material.getFilePath();
    }
    public String downloadMaterialUnsecured(Long materialId) {

        StudyMaterial material = materialRepository.findById(materialId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Study Material not found"));

        return material.getFilePath();
    }

    public List<StudyMaterial> getMaterialsByTeacher(Long teacherId) {
        return materialRepository.findByTeacherId(teacherId);
    }
}