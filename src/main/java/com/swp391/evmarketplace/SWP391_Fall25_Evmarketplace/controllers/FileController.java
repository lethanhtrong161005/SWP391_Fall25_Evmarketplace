package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.StoredFile;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
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
    public ResponseEntity<Resource> getVideo(@PathVariable String name) {
        Resource resource = fileService.loadVideoAsResource(name);
        String contentType = guessContentType(resource);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionInline(name))
                .body(resource);
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

    private String contentDispositionInline(String filename) {
        return ContentDisposition.inline().filename(filename).build().toString();
    }

    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<String> handleBusiness(CustomBusinessException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
