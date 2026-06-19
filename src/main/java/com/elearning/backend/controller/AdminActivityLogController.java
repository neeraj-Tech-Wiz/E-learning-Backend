package com.elearning.backend.controller;

import com.elearning.backend.model.ActivityLog;
import com.elearning.backend.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/activity-logs")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminActivityLogController {

    private final ActivityLogService service;

    @GetMapping
    public Page<ActivityLog> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getLogs(page, size);
    }
}
