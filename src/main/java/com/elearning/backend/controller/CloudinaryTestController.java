package com.elearning.backend.controller;

import com.elearning.backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class CloudinaryTestController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam MultipartFile file) {
        try {

            String url;

            String type = file.getContentType();

            if (type != null && type.startsWith("image")) {
                url = cloudinaryService.uploadImage(file);

            } else if (type != null && type.startsWith("video")) {
                url = cloudinaryService.uploadVideo(file);

            } else {
                url = cloudinaryService.uploadPdf(file);
            }

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "url", url
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "error", e.getMessage()
                    )
            );
        }
    }
}