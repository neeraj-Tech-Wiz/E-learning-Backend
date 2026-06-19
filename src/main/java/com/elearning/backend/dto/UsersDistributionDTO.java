package com.elearning.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsersDistributionDTO {
    private String role;
    private long count;
}
