package com.elearning.backend.service;

import com.elearning.backend.dto.*;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Student;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.security.service.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;
    private final AuthenticatedUserService authenticatedUserService;

    public Student findStudentByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated student not found."));
    }

    public Student registerNewStudent(Student student) {
        String rawPassword = student.getPassword();
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        student.setPassword(passwordEncoder.encode(rawPassword));
        return studentRepository.save(student);
    }

    // ---- Admin-specific methods ----

    public StudentAdminDto createStudent(CreateStudentRequest req) {

        Student student = new Student();
        student.setName(req.getName());
        student.setStandard(req.getStandard());
        student.setEmail(req.getEmail());
        student.setPassword(passwordEncoder.encode(req.getPassword()));

        Student saved = studentRepository.save(student);

        // 🔔 ACTIVITY LOG
        activityLogService.log(
                authenticatedUserService.getAuthenticatedUserEmail(),
                authenticatedUserService.getAuthenticatedUserRole(),
                "CREATE_STUDENT",
                "STUDENT",
                saved.getId().toString(),
                "Admin created student " + saved.getName()
        );

        return toAdminDto(saved);
    }


    public PageResponse<StudentAdminDto> getStudents(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Student> result;

        if (search != null && !search.isBlank()) {
            result = studentRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            result = studentRepository.findAll(pageable);
        }

        return new PageResponse<>(
                result.getContent().stream().map(this::toAdminDto).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public StudentAdminDto updateStudent(Long id, UpdateStudentRequest req) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        student.setName(req.getName());
        student.setStandard(req.getStandard());
        student.setEmail(req.getEmail());

        Student updated = studentRepository.save(student);

        // 🔔 ACTIVITY LOG
        activityLogService.log(
                authenticatedUserService.getAuthenticatedUserEmail(),
                authenticatedUserService.getAuthenticatedUserRole(),
                "UPDATE_STUDENT",
                "STUDENT",
                updated.getId().toString(),
                "Admin updated student " + updated.getName()
        );

        return toAdminDto(updated);
    }


    public void deleteStudent(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        studentRepository.deleteById(id);

        // 🔔 ACTIVITY LOG
        activityLogService.log(
                authenticatedUserService.getAuthenticatedUserEmail(),
                authenticatedUserService.getAuthenticatedUserRole(),
                "DELETE_STUDENT",
                "STUDENT",
                id.toString(),
                "Admin deleted student " + student.getName()
        );
    }

    public void resetStudentPassword(Long id, String newPassword) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        student.setPassword(passwordEncoder.encode(newPassword));
        studentRepository.save(student);

        // 🔔 ACTIVITY LOG
        activityLogService.log(
                authenticatedUserService.getAuthenticatedUserEmail(),
                authenticatedUserService.getAuthenticatedUserRole(),
                "RESET_STUDENT_PASSWORD",
                "STUDENT",
                id.toString(),
                "Admin reset password for student " + student.getName()
        );
    }


    private StudentAdminDto toAdminDto(Student s) {
        return new StudentAdminDto(
                s.getId(),
                s.getName(),
                s.getStandard(),
                s.getEmail()
        );
    }

    public List<StudentBasicDTO> getStudentsByStandard(int standard) {

        List<Student> students = studentRepository.findByStandard(standard);

        return students.stream()
                .map(s -> new StudentBasicDTO(
                        s.getId(),
                        s.getName(),
                        s.getStandard(),
                        s.getEmail()
                ))
                .toList();
    }

}
