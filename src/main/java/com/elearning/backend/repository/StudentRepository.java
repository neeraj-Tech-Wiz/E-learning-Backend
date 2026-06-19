package com.elearning.backend.repository;
import com.elearning.backend.model.Student;
//import org.hibernate.query.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
//    Optional<Student> findByEmail(String email);
//
//    List<Student> findByStandard(int standard);
//
//    List<Student> findByNameContainingIgnoreCase(String name);
Optional<Student> findByEmail(String email);
    List<Student> findByStandard(int standard);


    Page<Student> findByStandard(int standard, Pageable pageable);

    Page<Student> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Student> findAll(Pageable pageable);
}
