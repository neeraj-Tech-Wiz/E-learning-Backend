package com.elearning.backend.repository;
import com.elearning.backend.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Custom query methods
    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByTeacherId(Long teacherId);

    List<Attendance> findByDate(LocalDate date);
//    * SCENARIO 1: Counts "present" days for ONE student within a date range.
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.present = true AND a.date BETWEEN :startDate AND :endDate")    long countPresentByStudentAndDateRange(
            @Param("studentId") Long studentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT a.student.id, COUNT(a) FROM Attendance a " +
            "WHERE a.student.id IN :studentIds " +
            "AND a.present = true " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "GROUP BY a.student.id")
    List<Object[]> countPresentForStudentListInDateRange(
            @Param("studentIds") List<Long> studentIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
                SELECT\s
                    FUNCTION('date_part', 'month', a.date),
                    COUNT(a)
                FROM Attendance a
                WHERE a.present = true
                  AND FUNCTION('date_part', 'year', a.date) = :year
                GROUP BY FUNCTION('date_part', 'month', a.date)
                ORDER BY FUNCTION('date_part', 'month', a.date)
           \s""")
    List<Object[]> getMonthlyAttendanceStats(@Param("year") int year);

    @Query("""
                SELECT\s
                    t.id,
                    t.name,
                    FUNCTION('date_part', 'month', a.date),
                    COUNT(a)
                FROM Attendance a
                JOIN a.teacher t
                WHERE a.present = true
                  AND FUNCTION('date_part', 'year', a.date) = :year
                GROUP BY t.id, t.name, FUNCTION('date_part', 'month', a.date)
                ORDER BY t.name, FUNCTION('date_part', 'month', a.date)
           \s""")
    List<Object[]> getTeacherAttendanceHeatmap(@Param("year") int year);

}