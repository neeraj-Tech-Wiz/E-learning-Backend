package com.elearning.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateStudentProfileRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String address;
}
