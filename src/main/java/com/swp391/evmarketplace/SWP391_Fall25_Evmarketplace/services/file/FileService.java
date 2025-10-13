package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.StoredFile;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileUploadProperties props;

    private Path imagesRoot;
    private Path videosRoot;

    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;     // 10MB
    private static final long MAX_VIDEO_SIZE = 200L * 1024 * 1024;    // 200MB

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/webp",
            "image/gif"
    );

    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4",
            "video/webm",
            "video/ogg",
            "video/quicktime"
    );

    @PostConstruct
    public void init() throws IOException {
        imagesRoot = Paths.get(props.getRootPath().getImages()).toAbsolutePath().normalize();
        videosRoot = Paths.get(props.getRootPath().getVideo()).toAbsolutePath().normalize();

        ensureDir(imagesRoot); // NEW
        ensureDir(videosRoot); // NEW

        log.info("Image root: {}", imagesRoot);
        log.info("Video root: {}", videosRoot);
    }

    /* ===================== PUBLIC APIs ===================== */

    /** IMAGES: always unique by suffix + atomic write (no overwrite) */
    public StoredFile storeImage(MultipartFile file) throws IOException {
        validateFile(file, ALLOWED_IMAGE_TYPES, MAX_IMAGE_SIZE);

        // NEW: đảm bảo thư mục tồn tại ngay trước khi ghi
        ensureDir(imagesRoot);

        String originalName = sanitizeFilename(requireFilename(file));
        String ext  = toExt(originalName);
        String base = FilenameUtils.getBaseName(originalName);

        Path target = null;
        String storedName = null;

        final int MAX_RETRY = 5;
        int attempt = 0;

        while (attempt < MAX_RETRY) {
            String suffix = "_" + UUID.randomUUID().toString().substring(0, 8);
            storedName = base + suffix + ext;
            target = imagesRoot.resolve(storedName).normalize();

            if (!target.startsWith(imagesRoot)) {
                throw new CustomBusinessException("Invalid file path (path traversal attempt detected)");
            }

            // NEW: đảm bảo parent tồn tại (trong trường hợp sau này bạn tách theo ngày/tháng)
            ensureDir(target.getParent());

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target); // không REPLACE_EXISTING để giữ uniqueness
                break;
            } catch (FileAlreadyExistsException e) {
                attempt++;
                target = null;
            }
        }

        if (target == null) {
            throw new CustomBusinessException("Failed to generate a unique filename for the image");
        }

        return buildResult(originalName, target, contentTypeOf(file), file.getSize());
    }

    public StoredFile storeVideo(MultipartFile file) throws IOException {
        validateFile(file, ALLOWED_VIDEO_TYPES, MAX_VIDEO_SIZE);

        // NEW: đảm bảo thư mục tồn tại
        ensureDir(videosRoot);

        String originalName = sanitizeFilename(requireFilename(file));
        String ext = toExt(originalName);
        String base = FilenameUtils.getBaseName(originalName);

        String suffix = "_" + UUID.randomUUID().toString().substring(0, 8);
        String storedName = base + suffix + ext;

        Path target = videosRoot.resolve(storedName).normalize();
        while (Files.exists(target)) {
            suffix = "_" + UUID.randomUUID().toString().substring(0, 8);
            storedName = base + suffix + ext;
            target = videosRoot.resolve(storedName).normalize();
        }

        write(file, target);
        return buildResult(originalName, target, contentTypeOf(file), file.getSize());
    }

    public Resource loadImageAsResource(String storedFilename) {
        ensureDir(imagesRoot); // NEW: tránh lỗi nếu thư mục bị xóa ngoài ý muốn
        return loadAsResource(imagesRoot, storedFilename);
    }

    public Resource loadVideoAsResource(String storedFilename) {
        ensureDir(videosRoot);
        return loadAsResource(videosRoot, storedFilename);
    }

    public boolean deleteImage(String storedFilename) throws IOException {
        ensureDir(imagesRoot);
        return delete(imagesRoot, storedFilename);
    }

    public boolean deleteVideo(String storedFilename) throws IOException {
        ensureDir(videosRoot);
        return delete(videosRoot, storedFilename);
    }

    /* ===================== CORE / HELPERS ===================== */

    private void validateFile(MultipartFile file, Set<String> allowedContentTypes, long maxSize) {
        if (file == null || file.isEmpty()) {
            throw new CustomBusinessException("File is empty or does not exist");
        }
        if (file.getSize() > maxSize) {
            throw new CustomBusinessException("File size exceeds the allowed limit of " + maxSize + " bytes");
        }
        String ct = contentTypeOf(file);
        if (!allowedContentTypes.contains(ct)) {
            throw new CustomBusinessException("File type is not allowed: " + ct);
        }
    }

    private String requireFilename(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (!StringUtils.hasText(name) || !name.contains(".")) {
            throw new CustomBusinessException("File must have a valid name and extension (e.g., .jpg, .png, .mp4)");
        }
        return name;
    }

    private void write(MultipartFile file, Path target) throws IOException {
        Path root = target.startsWith(imagesRoot) ? imagesRoot : videosRoot;

        ensureDir(root);
        ensureDir(target.getParent());

        if (!target.normalize().startsWith(root)) {
            throw new CustomBusinessException("Invalid file path (path traversal attempt detected)");
        }
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void ensureDir(Path dir) {
        try {
            if (dir != null) Files.createDirectories(dir);
        } catch (IOException e) {
            throw new CustomBusinessException("Cannot create storage directory: " + dir + " (" + e.getMessage() + ")");
        }
    }

    private StoredFile buildResult(String originalName, Path target, String contentType, long size) {
        return new StoredFile(
                originalName,
                target.getFileName().toString(),
                contentType,
                size
        );
    }

    private Path resolveUniqueFilename(Path target) {
        Path parent = target.getParent();
        String base = FilenameUtils.getBaseName(target.getFileName().toString());
        String ext = toExt(target.getFileName().toString());

        int count = 1;
        Path cur = target;
        while (Files.exists(cur)) {
            cur = parent.resolve(base + "(" + count + ")" + ext);
            count++;
        }
        return cur;
    }

    private Resource loadAsResource(Path root, String storedFilename) {
        String safe = sanitizeFilename(storedFilename);
        Path file = root.resolve(safe).normalize();
        if (!file.startsWith(root)) {
            throw new CustomBusinessException("Invalid file path (path traversal attempt detected)");
        }
        return new FileSystemResource(file);
    }

    private boolean delete(Path root, String storedFilename) throws IOException {
        String safe = sanitizeFilename(storedFilename);
        Path file = root.resolve(safe).normalize();
        if (!file.startsWith(root)) {
            throw new CustomBusinessException("Invalid file path (path traversal attempt detected)");
        }
        return Files.deleteIfExists(file);
    }

    private String contentTypeOf(MultipartFile file) {
        return StringUtils.hasText(file.getContentType()) ? file.getContentType() : "application/octet-stream";
    }

    private String toExt(String filename) {
        String ext = FilenameUtils.getExtension(filename);
        if (!StringUtils.hasText(ext)) return "";
        if (ext.equalsIgnoreCase("exe") || ext.equalsIgnoreCase("sh") || ext.equalsIgnoreCase("bat")) {
            return ".bin";
        }
        return "." + ext.toLowerCase();
    }

    private String sanitizeFilename(String input) {
        if (!StringUtils.hasText(input)) return "unknown";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String safe = normalized.replaceAll("[/\\\\]", "_")
                .replaceAll("[^A-Za-z0-9._-]", "_");
        if (safe.length() > 200) safe = safe.substring(safe.length() - 200);
        return safe;
    }
}
