package com.elearning.backend.security.service;

import com.elearning.backend.model.Student;
import com.elearning.backend.model.Teacher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    public String getAuthenticatedUserEmail() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        throw new IllegalStateException("User not authenticated");
    }

    public Long getAuthenticatedUserId() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (principal instanceof Student student) return student.getId();
        if (principal instanceof Teacher teacher) return teacher.getId();

        throw new IllegalStateException("User type not recognized");
    }

    public String getAuthenticatedUserRole() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("UNKNOWN");
    }
}
