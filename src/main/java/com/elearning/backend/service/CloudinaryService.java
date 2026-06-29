package com.elearning.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.elearning.backend.dto.CloudinaryUploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Path;import java.io.File;
import java.nio.file.Path;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Generic upload method used internally.
     */
    private CloudinaryUploadResult upload(MultipartFile file, String resourceType) throws IOException {

        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", resourceType
                )
        );

        return new CloudinaryUploadResult(
                result.get("secure_url").toString(),
                result.get("public_id").toString()
        );
    }

    /**
     * Upload Image
     */
    public CloudinaryUploadResult uploadImage(MultipartFile file) throws IOException {
        return upload(file, "image");
    }

    /**
     * Upload PDF / DOCX / Other Documents
     */
    public CloudinaryUploadResult uploadPdf(MultipartFile file) throws IOException {
        return upload(file, "raw");
    }

    /**
     * Upload Video
     */
    public CloudinaryUploadResult uploadVideo(MultipartFile file) throws IOException {
        return upload(file, "video");
    }

    /**
     * Generic upload from local file (used for migration)
     */
    public CloudinaryUploadResult uploadFile(Path filePath, String resourceType) throws IOException {

        File file = filePath.toFile();

        Map<?, ?> result = cloudinary.uploader().upload(
                file,
                ObjectUtils.asMap(
                        "resource_type", resourceType
                )
        );

        return new CloudinaryUploadResult(
                result.get("secure_url").toString(),
                result.get("public_id").toString()
        );
    }

    /**
     * Upload local PDF/DOC/DOCX
     */
    public CloudinaryUploadResult uploadPdf(Path filePath) throws IOException {
        return uploadFile(filePath, "raw");
    }
    /**
     * Upload any document (PDF, DOC, DOCX, PPT, ZIP, etc.)
     */
    public CloudinaryUploadResult uploadDocument(Path filePath) throws IOException {
        return uploadFile(filePath, "raw");
    }

    /**
     * Upload local Video
     */
    public CloudinaryUploadResult uploadVideo(Path filePath) throws IOException {
        return uploadFile(filePath, "video");
    }

    /**
     * Upload local Image
     */
    public CloudinaryUploadResult uploadImage(Path filePath) throws IOException {
        return uploadFile(filePath, "image");
    }

    /**
     * Delete file from Cloudinary
     */
    public void deleteFile(String publicId) throws IOException {

        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.emptyMap()
        );
    }
}