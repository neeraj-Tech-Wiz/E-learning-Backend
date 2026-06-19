package com.elearning.backend.dto;

public class VideoRoomDTO {

    private Long    id;
    private String  title;
    private String  roomUrl;
    private String  teacherName;  // pulled from Teacher model
    private Integer standard;
    private boolean active;
    private String  createdAt;

    public Long    getId()                      { return id; }
    public void    setId(Long id)               { this.id = id; }

    public String  getTitle()                   { return title; }
    public void    setTitle(String title)       { this.title = title; }

    public String  getRoomUrl()                 { return roomUrl; }
    public void    setRoomUrl(String url)       { this.roomUrl = url; }

    public String  getTeacherName()             { return teacherName; }
    public void    setTeacherName(String name)  { this.teacherName = name; }

    public Integer getStandard()               { return standard; }
    public void    setStandard(Integer s)       { this.standard = s; }

    public boolean isActive()                  { return active; }
    public void    setActive(boolean active)   { this.active = active; }

    public String  getCreatedAt()               { return createdAt; }
    public void    setCreatedAt(String c)       { this.createdAt = c; }
}