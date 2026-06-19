package com.elearning.backend.repository;

import com.elearning.backend.model.Teacher;
import com.elearning.backend.model.VideoRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRoomRepository extends JpaRepository<VideoRoom, Long> {

    /* All rooms for a specific teacher, newest first */
    List<VideoRoom> findByTeacherOrderByCreatedAtDesc(Teacher teacher);

    /* Only active rooms for a given standard, newest first */
    List<VideoRoom> findByStandardAndActiveTrueOrderByCreatedAtDesc(Integer standard);
}