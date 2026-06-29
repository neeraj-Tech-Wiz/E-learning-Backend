package com.elearning.backend.migration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
public class MigrationRunner implements CommandLineRunner {

    private final StudyMaterialMigrationService studyMaterialMigrationService;
    private final VideoMigrationService videoMigrationService;

    @Override
    public void run(String... args) {

        System.out.println("\n================================");
        System.out.println("STARTING CLOUDINARY MIGRATION");
        System.out.println("================================");

        studyMaterialMigrationService.migrateAll();

        videoMigrationService.migrateAll();

        System.out.println("\n================================");
        System.out.println("ALL MIGRATIONS COMPLETED");
        System.out.println("================================");
    }
}