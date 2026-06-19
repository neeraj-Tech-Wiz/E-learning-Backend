package com.elearning.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentProfileDto {

    private Long id;
    private String name;
    private String email;
    private int standard;
    private String address;
    private String profilePhoto;

}
