package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.StoredFile;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoredFile> uploadImage(@RequestPart("file") MultipartFile file) throws IOException {
        StoredFile saved = fileService.storeImage(file);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{name}")
                .buildAndExpand(saved.getStoredName())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping("/images/{name}")
    public ResponseEntity<Resource> getImage(@PathVariable String name) {
        Resource resource = fileService.loadImageAsResource(name);
        String contentType = guessContentType(resource);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionInline(name))
                .body(resource);
    }

    @DeleteMapping("/images/{name}")
    public ResponseEntity<Void> deleteImage(@PathVariable String name) throws IOException {
        boolean deleted = fileService.deleteImage(name);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    @PostMapping(value = "/videos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoredFile> uploadVideo(@RequestPart("file") MultipartFile file) throws IOException {
        StoredFile saved = fileService.storeVideo(file);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{name}")
                .buildAndExpand(saved.getStoredName())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping("/videos/{name}")
    public ResponseEntity<Resource> getVideo(
            @PathVariable String name,
            @RequestHeader(value = "Range", required = false) String rangeHeader
    ) throws IOException {
        Resource video = fileService.loadVideoAsResource(name);
        Path path = video.getFile().toPath();
        long fileLength = Files.size(path);

        // Lấy MIME type
        String contentType = Files.probeContentType(path);
        if (contentType == null) contentType = "video/mp4";

        // Nếu trình duyệt gửi Range header (vd: bytes=0-)
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            long start = 0, end = fileLength - 1;
            String[] ranges = rangeHeader.substring(6).split("-");
            try {
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException ignored) {}
            if (end >= fileLength) end = fileLength - 1;

            long chunkSize = end - start + 1;
            InputStream inputStream = Files.newInputStream(path);
            inputStream.skip(start);
            Resource partialResource = new InputStreamResource(inputStream);

            return ResponseEntity.status(206)
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", start, end, fileLength))
                    .contentLength(chunkSize)
                    .body(partialResource);
        }

        // Nếu không có Range -> trả full file
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .contentLength(fileLength)
                .body(video);
    }

    @DeleteMapping("/videos/{name}")
    public ResponseEntity<Void> deleteVideo(@PathVariable String name) throws IOException {
        boolean deleted = fileService.deleteVideo(name);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private String guessContentType(Resource resource) {
        try {
            if (resource.isFile()) {
                Path p = resource.getFile().toPath();
                String ct = Files.probeContentType(p);
                if (ct != null) return ct;
            }
        } catch (Exception ignored) {}
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    @GetMapping("/contract/{name}")
    public ResponseEntity<Resource> getContract(
            @PathVariable String name
    ) throws IOException {
        Resource resource = fileService.loadContractAsResource(name);
        String contentType = guessContentType(resource);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionInline(name))
                .body(resource);
    }

    private String contentDispositionInline(String filename) {
        return ContentDisposition.inline().filename(filename).build().toString();
    }

    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<String> handleBusiness(CustomBusinessException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
