package com.elearning.backend.migration;

import com.elearning.backend.dto.CloudinaryUploadResult;
import com.elearning.backend.model.VideoLecture;
import com.elearning.backend.repository.VideoLectureRepository;
import com.elearning.backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoMigrationService {

    private final VideoLectureRepository videoLectureRepository;
    private final FileLocatorService fileLocatorService;
    private final CloudinaryService cloudinaryService;

    public void migrateAll() {

        List<VideoLecture> videos = videoLectureRepository.findAll();

        int success = 0;
        int skipped = 0;
        int failed = 0;

        System.out.println("\n========== VIDEO MIGRATION ==========\n");

        for (VideoLecture video : videos) {

            try {

                String storedPath = video.getFilePath();

                if (storedPath == null || storedPath.isBlank()) {
                    skipped++;
                    continue;
                }

                // Already migrated
                if (storedPath.startsWith("http")) {

                    System.out.println("⏭ Already migrated : " + video.getTitle());

                    skipped++;
                    continue;
                }

                Optional<Path> file = fileLocatorService.locate(storedPath);

                if (file.isEmpty()) {

                    System.out.println("❌ File not found : " + storedPath);

                    failed++;
                    continue;
                }

                System.out.println("Uploading : " + file.get().getFileName());

                CloudinaryUploadResult result =
                        cloudinaryService.uploadVideo(file.get());

                video.setFilePath(result.getUrl());

                videoLectureRepository.save(video);

                System.out.println("✅ Success : " + video.getTitle());

                success++;

            }
            catch (Exception e) {

                failed++;

                System.out.println("❌ Failed : " + video.getTitle());

                e.printStackTrace();
            }

        }

        System.out.println("\n========================================");
        System.out.println("Video Migration Finished");
        System.out.println("Success : " + success);
        System.out.println("Skipped : " + skipped);
        System.out.println("Failed  : " + failed);
        System.out.println("========================================");
    }

}