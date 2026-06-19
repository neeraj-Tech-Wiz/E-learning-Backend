package com.elearning.backend.service;

import com.elearning.backend.dto.ProgressStatusDTO;
import com.elearning.backend.dto.StudentProgressSummaryDTO;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Student;
import com.elearning.backend.model.StudentProgress;
import com.elearning.backend.model.StudyMaterial;
import com.elearning.backend.model.VideoLecture;
import com.elearning.backend.repository.StudentProgressRepository;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.StudyMaterialRepository;
import com.elearning.backend.repository.VideoLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final StudentProgressRepository studentProgressRepository;
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    private final StudyMaterialRepository studyMaterialRepository;
    private final VideoLectureRepository videoLectureRepository;


    // ----------------------------- STUDENT MARK COMPLETE ----------------------------- //

    public StudentProgress markContentComplete(String studentEmail, Long contentId, String contentType) {

        Student student = studentService.findStudentByEmail(studentEmail);
        Long studentId = student.getId();

        Optional<StudentProgress> existing =
                studentProgressRepository.findByStudentIdAndContentIdAndContentType(
                        studentId, contentId, contentType
                );

        StudentProgress progress = existing.orElseGet(StudentProgress::new);

        progress.setStudent(student);
        progress.setContentId(contentId);
        progress.setContentType(contentType);
        progress.setCompleted(true);
        progress.setCompletionDate(LocalDateTime.now());

        return studentProgressRepository.save(progress);
    }


    // ----------------------------- STUDENT PROGRESS (SELF) ----------------------------- //

    public List<ProgressStatusDTO> getProgressStatus(String studentEmail) {

        Student student = studentService.findStudentByEmail(studentEmail);
        List<StudentProgress> records = studentProgressRepository.findByStudentId(student.getId());

        return records.stream()
                .map(this::mapToProgressStatusDTO)
                .collect(Collectors.toList());
    }


    private ProgressStatusDTO mapToProgressStatusDTO(StudentProgress progress) {
        ProgressStatusDTO dto = new ProgressStatusDTO();

        dto.setContentId(progress.getContentId());
        dto.setContentType(progress.getContentType());
        dto.setIsCompleted(progress.isCompleted());
        dto.setCompletionDate(progress.getCompletionDate());

        if (progress.getContentType().equals("LECTURE")) {
            videoLectureRepository.findById(progress.getContentId())
                    .ifPresent(v -> {
                        dto.setTitle(v.getTitle());
                        dto.setSubject(v.getSubject());
                    });
        } else {
            studyMaterialRepository.findById(progress.getContentId())
                    .ifPresent(m -> {
                        dto.setTitle(m.getTitle());
                        dto.setSubject(m.getSubject());
                    });
        }

        return dto;
    }


    // ----------------------------- TEACHER FULL GROUP VIEW ----------------------------- //

    public List<StudentProgressSummaryDTO> getAllStudentsProgress() {
        return studentRepository.findAll().stream()
                .map(this::buildStudentProgressSummary)
                .collect(Collectors.toList());
    }

    public List<StudentProgressSummaryDTO> searchStudentProgress(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name, Pageable.unpaged()).stream()
                .map(this::buildStudentProgressSummary)
                .collect(Collectors.toList());
    }


    // ----------------------------- FIXED LOGIC FOR TEACHER SUMMARY ----------------------------- //

    private StudentProgressSummaryDTO buildStudentProgressSummary(Student student) {

        Long studentId = student.getId();
        Integer standard = student.getStandard();

        // ✅ Fetch ALL content for student's class (correct!)
        List<VideoLecture> totalLecturesList =
                videoLectureRepository.findByTargetStandard(standard);

        List<StudyMaterial> totalMaterialsList =
                studyMaterialRepository.findByTargetStandard(standard);

        int totalLectures = totalLecturesList.size();
        int totalMaterials = totalMaterialsList.size();

        // ✅ Fetch completed progress only
        List<StudentProgress> records =
                studentProgressRepository.findByStudentId(studentId);

        long completedLectures =
                records.stream().filter(r -> r.getContentType().equals("LECTURE") && r.isCompleted()).count();

        long completedMaterials =
                records.stream().filter(r -> r.getContentType().equals("MATERIAL") && r.isCompleted()).count();

        // ✅ Accurate overall percentage
        int totalContent = totalLectures + totalMaterials;
        int completedContent = (int) (completedLectures + completedMaterials);

        double percentage = totalContent == 0 ? 0 : (completedContent * 100.0 / totalContent);


        // BUILD DTO
        StudentProgressSummaryDTO dto = new StudentProgressSummaryDTO();
        dto.setStudentId(studentId);
        dto.setStudentName(student.getName());
        dto.setStudentStandard(standard);

        dto.setTotalLectures((long) totalLectures);
        dto.setCompletedLectures(completedLectures);

        dto.setTotalMaterials((long) totalMaterials);
        dto.setCompletedMaterials(completedMaterials);

        dto.setOverallPercentage(percentage);

        dto.setProgressList(records.stream()
                .map(this::mapToProgressStatusDTO)
                .collect(Collectors.toList()));

        return dto;
    }


    public StudentProgressSummaryDTO getStudentProgressSummary(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return buildStudentProgressSummary(student);
    }
}
