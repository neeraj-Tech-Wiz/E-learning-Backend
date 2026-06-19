package com.elearning.backend.repository;
import com.elearning.backend.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher,Long> {
    Optional<Teacher> findByEmail(String email);
    Page<Teacher> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Teacher> findAll(Pageable pageable);
}
