package com.elearning.backend.repository;
import com.elearning.backend.model.AssignmentSubmission;
import com.elearning.backend.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findByStudentOrderBySubmittedAtDesc(Student student);
    List<AssignmentSubmission> findByAssignmentIdOrderBySubmittedAtDesc(Long assignmentId);
}