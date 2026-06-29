package com.elearning.backend.migration;

import com.elearning.backend.dto.CloudinaryUploadResult;
import com.elearning.backend.model.StudyMaterial;
import com.elearning.backend.repository.StudyMaterialRepository;
import com.elearning.backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyMaterialMigrationService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final FileLocatorService fileLocatorService;
    private final CloudinaryService cloudinaryService;

    public void migrateAll() {

        List<StudyMaterial> materials = studyMaterialRepository.findAll();

        int success = 0;
        int skipped = 0;
        int failed = 0;

        System.out.println("\n========== STUDY MATERIAL MIGRATION ==========\n");

        for (StudyMaterial material : materials) {

            try {

                String storedPath = material.getFilePath();

                if (storedPath == null || storedPath.isBlank()) {
                    skipped++;
                    continue;
                }

                // Skip already migrated files
                if (storedPath.startsWith("http")) {
                    System.out.println("⏭ Already migrated: " + material.getTitle());
                    skipped++;
                    continue;
                }

                Optional<Path> file = fileLocatorService.locate(storedPath);

                if (file.isEmpty()) {
                    System.out.println("❌ File not found: " + storedPath);
                    failed++;
                    continue;
                }

                System.out.println("Uploading: " + file.get().getFileName());

                CloudinaryUploadResult result =
                        cloudinaryService.uploadDocument(file.get());

                // Update database
                material.setFilePath(result.getUrl());

                studyMaterialRepository.save(material);

                System.out.println("✅ Success: " + material.getTitle());

                success++;

            } catch (Exception e) {

                failed++;

                System.out.println("❌ Failed: " + material.getTitle());
                e.printStackTrace();
            }

        }

        System.out.println("\n========================================");
        System.out.println("Migration Finished");
        System.out.println("Success : " + success);
        System.out.println("Skipped : " + skipped);
        System.out.println("Failed  : " + failed);
        System.out.println("========================================");
    }
}