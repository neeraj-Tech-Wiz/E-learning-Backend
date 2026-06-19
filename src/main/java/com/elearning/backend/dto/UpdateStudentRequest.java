// UpdateStudentRequest.java
package com.elearning.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateStudentRequest {

    @NotBlank
    private String name;

    @Min(1)
    private int standard;

    @Email
    @NotBlank
    private String email;
}