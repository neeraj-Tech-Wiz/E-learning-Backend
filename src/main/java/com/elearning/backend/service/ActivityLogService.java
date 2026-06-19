package com.elearning.backend.service;

import com.elearning.backend.model.ActivityLog;
import com.elearning.backend.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository repository;

    public void log(
            String actorEmail,
            String actorRole,
            String action,
            String entityType,
            String entityId,
            String description
    ) {
        ActivityLog log = ActivityLog.builder()
                .actorEmail(actorEmail)
                .actorRole(actorRole)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .description(description)
                .timestamp(LocalDateTime.now())
                .build();

        repository.save(log);
    }

    public Page<ActivityLog> getLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return repository.findAll(pageable);
    }
}
