package com.elearning.backend.service;

import com.elearning.backend.dto.CreateTeacherRequest;
import com.elearning.backend.dto.PageResponse;
import com.elearning.backend.dto.TeacherAdminDto;
import com.elearning.backend.dto.UpdateTeacherRequest;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Teacher;
import com.elearning.backend.repository.TeacherRepository;
import com.elearning.backend.security.service.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;
    private final AuthenticatedUserService authenticatedUserService;

    // =====================================================================
    // 1. Original teacher registration (used for signup)
    // =====================================================================
    public Teacher registerNewTeacher(Teacher teacher) {

        if (teacher.getPassword() == null || teacher.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required for teacher registration.");
        }

        // Encode password
        teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));

        // Save teacher
        Teacher savedTeacher = teacherRepository.save(teacher);

        // 🔔 Activity Log (ADMIN action)
        activityLogService.log(
                authenticatedUserService.getAuthenticatedUserEmail(),
                authenticatedUserService.getAuthenticatedUserRole(),
                "CREATE_TEACHER",
                "TEACHER",
                savedTeacher.getId().toString(),
                "Admin created teacher " + savedTeacher.getName()
        );

        return savedTeacher;
    }


    // =====================================================================
    // 2. Create Teacher (ADMIN)
    // =====================================================================
    public TeacherAdminDto createTeacher(CreateTeacherRequest req) {

        Teacher teacher = new Teacher();
        teacher.setName(req.getName());
        teacher.setEmail(req.getEmail());
        teacher.setPassword(passwordEncoder.encode(req.getPassword()));

        Teacher saved = teacherRepository.save(teacher);

        // 🔔 ACTIVITY LOG (ADMIN ACTION)
        activityLogService.log(
                authenticatedUserService.getAuthenticatedUserEmail(),
                authenticatedUserService.getAuthenticatedUserRole(),
                "CREATE_TEACHER",
                "TEACHER",
                saved.getId().toString(),
                "Admin created teacher " + saved.getName()
        );

        return toAdminDto(saved);
    }


    // =====================================================================
    // 3. Get Teachers (Paginated + Search)
    // =====================================================================
    public PageResponse<TeacherAdminDto> getTeachers(int page, int size, String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Teacher> result;

        if (search != null && !search.isBlank()) {
            result = teacherRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            result = teacherRepository.findAll(pageable);
        }

        return new PageResponse<>(
                result.getContent().stream().map(this::toAdminDto).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    // =====================================================================
    // 4. Update Teacher (ADMIN)
    // =====================================================================
    public TeacherAdminDto updateTeacher(Long id, UpdateTeacherRequest req) {

        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID " + id));

        teacher.setName(req.getName());
        teacher.setEmail(req.getEmail());

        Teacher updated = teacherRepository.save(teacher);

        // 🔔 ACTIVITY LOG
        activityLogService.log(
                authenticatedUserService.getAuthenticatedUserEmail(),
                authenticatedUserService.getAuthenticatedUserRole(),
                "UPDATE_TEACHER",
                "TEACHER",
                updated.getId().toString(),
                "Admin updated teacher " + updated.getName()
        );

        return toAdminDto(updated);
    }


    // =====================================================================
    // 5. Delete Teacher (ADMIN)
    // =====================================================================
    public void deleteTeacher(Long id) {

        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID " + id));

        teacherRepository.deleteById(id);

        // 🔔 ACTIVITY LOG
        activityLogService.log(
                authenticatedUserService.getAuthenticatedUserEmail(),
                authenticatedUserService.getAuthenticatedUserRole(),
                "DELETE_TEACHER",
                "TEACHER",
                id.toString(),
                "Admin deleted teacher " + teacher.getName()
        );
    }


    // =====================================================================
    // 6. Reset Password (ADMIN)
    // =====================================================================
    public void resetTeacherPassword(Long id, String newPassword) {

        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID " + id));

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        teacher.setPassword(passwordEncoder.encode(newPassword));
        teacherRepository.save(teacher);

        // 🔔 ACTIVITY LOG
        activityLogService.log(
                authenticatedUserService.getAuthenticatedUserEmail(),
                authenticatedUserService.getAuthenticatedUserRole(),
                "RESET_TEACHER_PASSWORD",
                "TEACHER",
                id.toString(),
                "Admin reset password for teacher " + teacher.getName()
        );
    }


    // =====================================================================
    // 7. Helper: Convert Teacher -> TeacherAdminDto
    // =====================================================================
    private TeacherAdminDto toAdminDto(Teacher teacher) {
        return new TeacherAdminDto(
                teacher.getId(),
                teacher.getName(),
                teacher.getEmail()
        );
    }
}
