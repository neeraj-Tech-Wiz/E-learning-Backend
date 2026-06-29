package com.elearning.backend.migration;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class FileLocatorService {

    private final Path uploadsRoot =
            Paths.get("uploads").toAbsolutePath().normalize();

    public Optional<Path> locate(String storedPath) {

        if (storedPath == null || storedPath.isBlank()) {
            return Optional.empty();
        }

        try {

            // Normalize separators
            String normalized = storedPath.replace("\\", "/");

            Path candidate = Paths.get(normalized).normalize();

            // Exact path exists?
            if (Files.exists(candidate)) {
                return Optional.of(candidate.toAbsolutePath());
            }

            // Relative to project root?
            candidate = Paths.get("").toAbsolutePath()
                    .resolve(normalized)
                    .normalize();

            if (Files.exists(candidate)) {
                return Optional.of(candidate);
            }

            // Search entire uploads folder using filename
            String filename = Paths.get(normalized)
                    .getFileName()
                    .toString();

            try (Stream<Path> stream = Files.walk(uploadsRoot)) {

                Optional<Path> result = stream
                        .filter(Files::isRegularFile)
                        .filter(path ->
                                path.getFileName()
                                        .toString()
                                        .equalsIgnoreCase(filename))
                        .findFirst();

                if (result.isPresent()) {
                    return result;
                }
            }

        } catch (IOException e) {

            e.printStackTrace();
        }

        return Optional.empty();
    }

}