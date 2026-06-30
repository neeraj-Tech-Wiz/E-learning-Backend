package com.elearning.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.elearning.backend.dto.CloudinaryUploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Generic upload for Images & Videos.
     */
    private CloudinaryUploadResult uploadBytes(MultipartFile file, String resourceType)
            throws IOException {

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
    public CloudinaryUploadResult uploadImage(MultipartFile file)
            throws IOException {

        return uploadBytes(file, "image");
    }

    /**
     * Upload Video
     */
    public CloudinaryUploadResult uploadVideo(MultipartFile file)
            throws IOException {

        return uploadBytes(file, "video");
    }

    /**
     * Upload PDF / DOC / DOCX / PPT etc.
     */
    public CloudinaryUploadResult uploadPdf(MultipartFile multipartFile)
            throws IOException {

        File tempFile = File.createTempFile(
                "upload-",
                "-" + multipartFile.getOriginalFilename()
        );

        multipartFile.transferTo(tempFile);

        try {

            Map<?, ?> result = cloudinary.uploader().upload(
                    tempFile,
                    ObjectUtils.asMap(
                            "resource_type", "raw",
                            "use_filename", true,
                            "unique_filename", true
                    )
            );

            return new CloudinaryUploadResult(
                    result.get("secure_url").toString(),
                    result.get("public_id").toString()
            );

        } finally {

            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * Generic upload from local file (Migration)
     */
    public CloudinaryUploadResult uploadFile(Path filePath, String resourceType)
            throws IOException {

        File file = filePath.toFile();

        Map<?, ?> result = cloudinary.uploader().upload(
                file,
                ObjectUtils.asMap(
                        "resource_type", resourceType,
                        "use_filename", true,
                        "unique_filename", true
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
    public CloudinaryUploadResult uploadPdf(Path filePath)
            throws IOException {

        return uploadFile(filePath, "raw");
    }

    /**
     * Upload any document
     */
    public CloudinaryUploadResult uploadDocument(Path filePath)
            throws IOException {

        return uploadFile(filePath, "raw");
    }

    /**
     * Upload local Video
     */
    public CloudinaryUploadResult uploadVideo(Path filePath)
            throws IOException {

        return uploadFile(filePath, "video");
    }

    /**
     * Upload local Image
     */
    public CloudinaryUploadResult uploadImage(Path filePath)
            throws IOException {

        return uploadFile(filePath, "image");
    }

    /**
     * Delete asset
     */
    public void deleteFile(String publicId)
            throws IOException {

        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.emptyMap()
        );
    }
}