package com.elearning.backend.service;

import com.elearning.backend.dto.CloudinaryUploadResult;
import com.elearning.backend.dto.VideoLectureDTO;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Student;
import com.elearning.backend.model.StudentProgress;
import com.elearning.backend.model.Teacher;
import com.elearning.backend.model.VideoLecture;
import com.elearning.backend.repository.StudentProgressRepository;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.TeacherRepository;
import com.elearning.backend.repository.VideoLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoLectureService {

    private final Path rootDir = Paths.get("uploads/video_lectures").toAbsolutePath().normalize();

    private final VideoLectureRepository videoLectureRepository;
    private final TeacherRepository teacherRepository;
    private final VideoLectureMapper videoLectureMapper;
    private final StudentRepository studentRepository;
    private final StudentProgressRepository studentProgressRepository;
    private final CloudinaryService cloudinaryService;

    public VideoLecture uploadVideo(
            String teacherEmail,
            String title,
            String duration,
            MultipartFile file,
            int targetStandard,
            String subject
    ) throws IOException {

        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Teacher not found with Email: " + teacherEmail
                        ));

        CloudinaryUploadResult uploadResult =
                cloudinaryService.uploadVideo(file);

        VideoLecture lecture = new VideoLecture();

        lecture.setTitle(title);
        lecture.setDuration(duration);

        // Store Cloudinary URL
        lecture.setFilePath(uploadResult.getUrl());

        lecture.setTeacher(teacher);
        lecture.setTargetStandard(targetStandard);
        lecture.setSubject(subject);

        return videoLectureRepository.save(lecture);
    }

public List<VideoLectureDTO> getVideosForStudent(String studentEmail, String searchSubject) {

    // 1️⃣ Fetch the Student entity securely using JWT email
    Student student = studentRepository.findByEmail(studentEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found for email: " + studentEmail));

    Integer studentStandard = student.getStandard();
    Long studentId = student.getId();

    // 2️⃣ Fetch all video lectures that match student's standard and subject
    List<VideoLecture> entities = videoLectureRepository
            .findByTargetStandardAndSubjectContainingIgnoreCase(
                    studentStandard,
                    searchSubject
            );

    // 3️⃣ Fetch all completed video lectures for this student
    List<StudentProgress> completedProgress = studentProgressRepository
            .findByStudentIdAndContentTypeAndIsCompletedTrue(studentId, "LECTURE");

    Set<Long> completedIds = completedProgress.stream()
            .map(StudentProgress::getContentId)
            .collect(Collectors.toSet());

    // 4️⃣ Map entities to DTOs and inject completion status
    return entities.stream()
            .map(video -> {
                VideoLectureDTO dto = videoLectureMapper.toDto(video);
                dto.setCompleted(completedIds.contains(video.getId())); // <-- Add this field
                return dto;
            })
            .collect(Collectors.toList());
}



    public String downloadVideo(Long lectureId) {

        VideoLecture lecture = videoLectureRepository.findById(lectureId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Video Lecture not found with ID: " + lectureId));

        return lecture.getFilePath();
    }
    public String downloadVideoSecurely(String studentEmail, Long lectureId)
            throws AccessDeniedException {

        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found for email: " + studentEmail));

        VideoLecture lecture = videoLectureRepository.findById(lectureId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Video Lecture not found with ID: " + lectureId));

        if (student.getStandard() != lecture.getTargetStandard()) {
            throw new AccessDeniedException(
                    "Access denied. Lecture is not intended for Standard "
                            + student.getStandard());
        }

        return lecture.getFilePath();
    }

    public List<VideoLecture> getVideosByTeacher(Long teacherId) {
        return videoLectureRepository.findByTeacherId(teacherId);
    }
}