package com.elearning.backend.controller;

import com.elearning.backend.dto.VideoRoomDTO;
import com.elearning.backend.service.VideoRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/video")
public class VideoRoomController {

    @Autowired
    private VideoRoomService videoRoomService;

    /* ── TEACHER: create a room ── */
    @PostMapping("/rooms")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<VideoRoomDTO> createRoom(
            @RequestBody CreateRoomRequest req,
            Principal principal
    ) {
        VideoRoomDTO room = videoRoomService.createRoom(req.getTitle(), principal.getName());
        return ResponseEntity.ok(room);
    }

    /* ── TEACHER: list their rooms ── */
    @GetMapping("/rooms/mine")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<List<VideoRoomDTO>> myRooms(Principal principal) {
        return ResponseEntity.ok(videoRoomService.getRoomsByTeacher(principal.getName()));
    }

    /* ── TEACHER: end / delete a room ──
       Using POST instead of DELETE to avoid Spring Security
       CSRF/method filtering issues on DELETE requests.       ── */
    @PostMapping("/rooms/{roomId}/end")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long roomId,
            Principal principal
    ) {
        videoRoomService.deleteRoom(roomId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    /* ── STUDENT: get active rooms for their standard ── */
    @GetMapping("/rooms/active")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<VideoRoomDTO>> activeRooms(Principal principal) {
        return ResponseEntity.ok(videoRoomService.getActiveRoomsForStudent(principal.getName()));
    }

    /* ── Request body ── */
    public static class CreateRoomRequest {
        private String title;
        public String getTitle()         { return title; }
        public void   setTitle(String t) { this.title = t; }
    }
}