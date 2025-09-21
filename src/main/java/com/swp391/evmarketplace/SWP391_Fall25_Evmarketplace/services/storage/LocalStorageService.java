package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.storage;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class LocalStorageService implements StorageService {
    @Value("${fileUpload.rootPath:uploads}")
    private String rootPath; // "uploads" (relative) hoặc "/data/uploads" (absolute)

    /* ============ helpers chung ============ */

    private Path bucketRoot(String bucket) {
        String b = sanitizeName(bucket);
        return Paths.get(rootPath, b);
    }

    private static String sanitizeName(String name) {
        String n = (name == null ? "" : name).trim();
        if (n.isBlank()) throw new CustomBusinessException("Invalid name");
        n = n.replaceAll("[^A-Za-z0-9._-]", "_");
        if (n.isBlank()) throw new CustomBusinessException("Invalid name");
        return n;
    }

    private static Path safeResolve(Path base, String child) {
        Path p = base.resolve(child).normalize();
        if (!p.startsWith(base)) throw new CustomBusinessException("Invalid path");
        return p;
    }

    private static void ensureDir(Path dir) {
        try {
            if (!Files.exists(dir)) Files.createDirectories(dir);
        } catch (IOException e) {
            throw new CustomBusinessException("Cannot create directory: " + dir + " - " + e.getMessage());
        }
    }

    private static String addSuffixIfExists(Path dir, String original) throws IOException {
        Path target = dir.resolve(original);
        int c = 1;
        while (Files.exists(target)) {
            String newName = original.replaceFirst("(\\.[^.]+)?$", "_" + c + "$1");
            target = dir.resolve(newName);
            c++;
        }
        return target.getFileName().toString();
    }

    private static Resource toResource(Path file) {
        try {
            Resource r = new UrlResource(file.toUri());
            if (!r.exists() || !r.isReadable()) throw new CustomBusinessException("File not found");
            return r;
        } catch (Exception e) {
            throw new CustomBusinessException("Load file error: " + e.getMessage());
        }
    }


    @Override
    public String save(String bucket, String key, MultipartFile file, boolean replaceAll) {
        try {
            String safeBucket = sanitizeName(bucket);
            String safeKey    = sanitizeName(key);

            Path dir = bucketRoot(safeBucket).resolve(safeKey);
            ensureDir(dir);

            if (replaceAll) {
                try (var s = Files.list(dir)) {
                    for (Path p : s.filter(Files::isRegularFile).toList()) {
                        Files.deleteIfExists(p);
                    }
                }
            }

            String original = sanitizeName(file.getOriginalFilename());
            String finalName = replaceAll
                    ? buildSingleName(original)          // avatar.ext
                    : addSuffixIfExists(dir, original);  // front.png -> front_1.png...

            Path target = safeResolve(dir, finalName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return finalName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomBusinessException("Save file failed: " + e.getMessage());
        }
    }

    private static String buildSingleName(String original) {
        String ext = original.contains(".") ? original.substring(original.lastIndexOf(".")) : "";
        return "file" + ext; // hoặc "avatar"+ext tuỳ controller dùng
    }

    @Override
    public Resource load(String bucket, String key, String filename) {
        String safeBucket = sanitizeName(bucket);
        String safeKey    = sanitizeName(key);

        Path dir = bucketRoot(safeBucket).resolve(safeKey);
        if (filename == null || filename.isBlank()) {
            // load file đầu tiên (dùng cho "single file" như avatar nếu muốn)
            if (!Files.exists(dir)) throw new CustomBusinessException("File not found");
            try (var s = Files.list(dir)) {
                Path p = s.filter(Files::isRegularFile)
                        .findFirst()
                        .orElseThrow(() -> new CustomBusinessException("File not found"));
                return toResource(p);
            } catch (IOException e) {
                throw new CustomBusinessException("Load file error: " + e.getMessage());
            }
        }
        Path file = safeResolve(dir, sanitizeName(filename));
        return toResource(file);
    }

    @Override
    public List<String> list(String bucket, String key) {
        String safeBucket = sanitizeName(bucket);
        String safeKey    = sanitizeName(key);

        Path dir = bucketRoot(safeBucket).resolve(safeKey);
        if (!Files.exists(dir)) return List.of();
        try (var s = Files.list(dir)) {
            return s.filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .toList();
        } catch (IOException e) {
            throw new CustomBusinessException("List files failed: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String bucket, String key, String filename) {
        String safeBucket = sanitizeName(bucket);
        String safeKey    = sanitizeName(key);

        Path dir = bucketRoot(safeBucket).resolve(safeKey);
        if (!Files.exists(dir)) return false;

        try {
            if (filename == null || filename.isBlank()) {
                boolean any = false;
                try (var s = Files.list(dir)) {
                    for (Path p : s.filter(Files::isRegularFile).toList()) {
                        any |= Files.deleteIfExists(p);
                    }
                }
                return any;
            } else {
                Path file = safeResolve(dir, sanitizeName(filename));
                return Files.deleteIfExists(file);
            }
        } catch (IOException e) {
            throw new CustomBusinessException("Delete file failed: " + e.getMessage());
        }
    }
}
