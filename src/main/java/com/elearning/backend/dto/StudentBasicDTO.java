package com.elearning.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentBasicDTO {
    private Long id;
    private String name;
    private int standard;
    private String email;
}
