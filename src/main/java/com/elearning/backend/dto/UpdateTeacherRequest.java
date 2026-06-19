// UpdateTeacherRequest.java
package com.elearning.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTeacherRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;
}