// TeacherAdminDto.java
package com.elearning.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherAdminDto {
    private Long id;
    private String name;
    private String email;
}