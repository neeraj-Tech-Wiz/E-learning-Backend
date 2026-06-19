package com.elearning.backend.repository;
import com.elearning.backend.model.Assignment;
import com.elearning.backend.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByTeacherOrderByCreatedAtDesc(Teacher teacher);
    List<Assignment> findByStandardAndActiveTrueOrderByCreatedAtDesc(Integer standard);
}