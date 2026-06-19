package com.elearning.backend.service;

import com.elearning.backend.dto.VideoRoomDTO;
import com.elearning.backend.model.Teacher;
import com.elearning.backend.model.Student;
import com.elearning.backend.model.VideoRoom;
import com.elearning.backend.repository.TeacherRepository;
import com.elearning.backend.repository.StudentRepository;
import com.elearning.backend.repository.VideoRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VideoRoomService {

    // ── Jitsi public server — 100% free, no account, no API key needed ──
    private static final String JITSI_BASE_URL = "https://meet.jit.si/";

    @Autowired private VideoRoomRepository videoRoomRepository;
    @Autowired private TeacherRepository   teacherRepository;
    @Autowired private StudentRepository   studentRepository;

    /* ══════════════════════════════════════════════════════
       CREATE ROOM
       No HTTP call to any external API needed —
       Jitsi rooms are created just by visiting a unique URL.
    ══════════════════════════════════════════════════════ */
    public VideoRoomDTO createRoom(String title, String teacherEmail) {

        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found: " + teacherEmail));

        // Generate a unique room name: eduverse-<short-uuid>
        // e.g.  eduverse-3f7a1b2c
        String roomName = "eduverse-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String roomUrl  = JITSI_BASE_URL + roomName;

        VideoRoom room = new VideoRoom();
        room.setTitle(title != null && !title.isBlank() ? title : "Class Session");
        room.setRoomUrl(roomUrl);
        room.setDailyRoomName(roomName); // field reused as "jitsi room name" — no rename needed
        room.setTeacher(teacher);
        room.setActive(true);
        room.setCreatedAt(LocalDateTime.now());

        // TODO: adjust getter to match your Teacher model field name
        // Common options: teacher.getStandard() / teacher.getClassStandard() / teacher.getStd()
        room.setStandard(teacher.getStandard());

        VideoRoom saved = videoRoomRepository.save(room);
        return toDTO(saved);
    }

    /* ══════════════════════════════════════════════════════
       DELETE ROOM  (mark inactive — Jitsi rooms need no deletion)
    ══════════════════════════════════════════════════════ */
    public void deleteRoom(Long roomId, String teacherEmail) {

        VideoRoom room = videoRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));

        // TODO: adjust to match your Teacher model getter (getEmail / getUsername)
        if (!room.getTeacher().getEmail().equals(teacherEmail)) {
            throw new RuntimeException("Not authorised to delete this room");
        }

        // Jitsi rooms expire automatically when everyone leaves —
        // we just mark inactive in our DB so students stop seeing it.
        room.setActive(false);
        videoRoomRepository.save(room);
    }

    /* ══════════════════════════════════════════════════════
       GET TEACHER'S ROOMS
    ══════════════════════════════════════════════════════ */
    public List<VideoRoomDTO> getRoomsByTeacher(String teacherEmail) {
        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found: " + teacherEmail));

        return videoRoomRepository
                .findByTeacherOrderByCreatedAtDesc(teacher)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /* ══════════════════════════════════════════════════════
       GET ACTIVE ROOMS FOR STUDENT
    ══════════════════════════════════════════════════════ */
    public List<VideoRoomDTO> getActiveRoomsForStudent(String studentEmail) {

        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentEmail));

        // TODO: adjust to match your Student model field name
        return videoRoomRepository
                .findByStandardAndActiveTrueOrderByCreatedAtDesc(student.getStandard())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /* ══════════════════════════════════════════════════════
       HELPER: Entity → DTO
    ══════════════════════════════════════════════════════ */
    private VideoRoomDTO toDTO(VideoRoom r) {
        VideoRoomDTO dto = new VideoRoomDTO();
        dto.setId(r.getId());
        dto.setTitle(r.getTitle());
        dto.setRoomUrl(r.getRoomUrl());
        dto.setStandard(r.getStandard());
        dto.setActive(r.isActive());
        dto.setCreatedAt(r.getCreatedAt().toString());

        Teacher t = r.getTeacher();
        if (t != null) {
            // TODO: adjust to match your Teacher model — getName() / getUsername() / getEmail()
            dto.setTeacherName(t.getName());
        }
        return dto;
    }
}