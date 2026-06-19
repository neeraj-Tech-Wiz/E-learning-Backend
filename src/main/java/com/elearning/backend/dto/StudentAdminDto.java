// StudentAdminDto.java
package com.elearning.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentAdminDto {
    private Long id;
    private String name;
    private int standard;
    private String email;
}