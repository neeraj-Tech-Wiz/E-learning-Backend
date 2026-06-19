package com.elearning.backend.service;

import com.elearning.backend.dto.StudentProfileDto;
import com.elearning.backend.dto.UpdateStudentProfileRequest;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Student;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.security.service.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentRepository studentRepository;
    private final AuthenticatedUserService authenticatedUserService;

    // =========================================================
    // GET logged-in student's profile
    // =========================================================
    public StudentProfileDto getMyProfile() {

        String email = authenticatedUserService.getAuthenticatedUserEmail();

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return new StudentProfileDto(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getStandard(),
                student.getAddress(),
                student.getProfilePhoto()
        );
    }
    private Student getAuthenticatedStudent() {
        String email = authenticatedUserService.getAuthenticatedUserEmail();
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated student not found"));
    }

    public void updateProfilePhoto(MultipartFile file) throws IOException {
        Student s = getAuthenticatedStudent();

        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path path = Paths.get("uploads/profile/", filename);

        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        s.setProfilePhoto(filename);
        studentRepository.save(s);
    }

    // =========================================================
    // UPDATE logged-in student's profile (email + address only)
    // =========================================================
    public StudentProfileDto updateMyProfile(UpdateStudentProfileRequest req) {

        String email = authenticatedUserService.getAuthenticatedUserEmail();

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        student.setEmail(req.getEmail());
        student.setAddress(req.getAddress());

        Student updated = studentRepository.save(student);

        return new StudentProfileDto(
                updated.getId(),
                updated.getName(),
                updated.getEmail(),
                updated.getStandard(),
                updated.getAddress(),
                updated.getProfilePhoto()
        );
    }
}
