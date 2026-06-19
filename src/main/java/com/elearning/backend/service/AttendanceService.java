package com.elearning.backend.service;

import com.elearning.backend.dto.AttendanceSubmissionDTO;
import com.elearning.backend.dto.StudentMonthlyAttendanceDTO;
import com.elearning.backend.exception.ResourceNotFoundException;
import com.elearning.backend.model.Attendance;
import com.elearning.backend.model.Student;
import com.elearning.backend.model.Teacher;
import com.elearning.backend.repository.AttendanceRepository;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentService studentService;

    // ============================================================
    // 1. BULK MARK ATTENDANCE
    // ============================================================
    public List<Attendance> bulkMarkAttendance(
            String teacherEmail,
            AttendanceSubmissionDTO submissionDTO) {

        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated teacher not found."));

        List<Attendance> newRecords = new ArrayList<>();

        for (AttendanceSubmissionDTO.StudentStatus status : submissionDTO.getStatuses()) {

            Student student = studentRepository.findById(status.getStudentId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Student not found: " + status.getStudentId()));

            Attendance attendance = new Attendance();

            attendance.setStudent(student);
            attendance.setTeacher(teacher);
            attendance.setAttendanceDate(submissionDTO.getDate());
            attendance.setPresent(status.isPresent());

            newRecords.add(attendance);
        }

        return attendanceRepository.saveAll(newRecords);
    }

    // ============================================================
    // 2. STUDENT PERSONAL MONTHLY ATTENDANCE REPORT
    // ============================================================
    public StudentMonthlyAttendanceDTO getStudentMonthlyReport(String studentEmail, int year, int month) {

        Student student = studentService.findStudentByEmail(studentEmail);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        int totalDaysInMonth = yearMonth.lengthOfMonth();

        long presentCount = attendanceRepository.countPresentByStudentAndDateRange(
                student.getId(), startDate, endDate);

        return new StudentMonthlyAttendanceDTO(
                student.getId(),
                student.getName(),
                (int) presentCount,
                totalDaysInMonth,
                month,
                year
        );
    }

    // ============================================================
    // 3. TEACHER DASHBOARD REPORT (PAGINATED)
    // ============================================================
    public Page<StudentMonthlyAttendanceDTO> getTeacherDashboardReport(
            String teacherEmail,
            int year,
            int month,
            int page,
            int size) {

        // 1️⃣ Find teacher
        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with email: " + teacherEmail));

        int standard = teacher.getStandard();

        // 2️⃣ Paginated list of students in teacher's standard
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> studentsPage = studentRepository.findByStandard(standard, pageable);

        List<Student> students = studentsPage.getContent();

        // 3️⃣ Calculate month range
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        int totalDaysInMonth = yearMonth.lengthOfMonth();

        // 4️⃣ Extract all student IDs
        List<Long> studentIds = students.stream()
                .map(Student::getId)
                .collect(Collectors.toList());

        if (studentIds.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, studentsPage.getTotalElements());
        }

        // 5️⃣ SQL optimized attendance counts
        List<Object[]> results = attendanceRepository.countPresentForStudentListInDateRange(
                studentIds, startDate, endDate);

        // Convert: Object[]{studentId, count} → Map<Long, Long>
        Map<Long, Long> presentCountMap = results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        // 6️⃣ Build DTO list
        List<StudentMonthlyAttendanceDTO> dtoList = students.stream()
                .map(student -> {
                    int presentCount = presentCountMap
                            .getOrDefault(student.getId(), 0L)
                            .intValue();

                    return new StudentMonthlyAttendanceDTO(
                            student.getId(),
                            student.getName(),
                            presentCount,
                            totalDaysInMonth,
                            month,
                            year
                    );
                }).collect(Collectors.toList());

        // 7️⃣ Return paginated DTO
        return new PageImpl<>(dtoList, pageable, studentsPage.getTotalElements());
    }
}
