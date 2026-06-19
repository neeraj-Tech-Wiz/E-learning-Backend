package com.elearning.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_rooms")
public class VideoRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "room_url", nullable = false, length = 512)
    private String roomUrl;

    @Column(name = "daily_room_name", nullable = false)
    private String dailyRoomName;

    /* ── Uses your existing Teacher model, NOT a generic User ── */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    /* ── Which standard (class) this room is for ── */
    @Column(nullable = false)
    private Integer standard;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ── Getters & Setters ──
    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }

    public String getTitle()                   { return title; }
    public void setTitle(String title)         { this.title = title; }

    public String getRoomUrl()                 { return roomUrl; }
    public void setRoomUrl(String roomUrl)     { this.roomUrl = roomUrl; }

    public String getDailyRoomName()           { return dailyRoomName; }
    public void setDailyRoomName(String name)  { this.dailyRoomName = name; }

    public Teacher getTeacher()                { return teacher; }
    public void setTeacher(Teacher teacher)    { this.teacher = teacher; }

    public Integer getStandard()               { return standard; }
    public void setStandard(Integer standard)  { this.standard = standard; }

    public boolean isActive()                 { return active; }
    public void setActive(boolean active)     { this.active = active; }

    public LocalDateTime getCreatedAt()               { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}