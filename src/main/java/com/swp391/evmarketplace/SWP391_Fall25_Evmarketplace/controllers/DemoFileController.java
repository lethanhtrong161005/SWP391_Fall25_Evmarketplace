package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.util.Map;

@RestController
@RequestMapping("/api/profiles/{accountId}/avatar")
@Tag(name = "Profile Avatar", description = "Upload/View/Delete profile avatar")
@RequiredArgsConstructor
public class DemoFileController {
    @Autowired
    private FileService fileService;
    @Value("${server.url}")
    private String serverUrl;

    // Upload/replace avatar (giữ đúng 1 file)
    @PostMapping
    public ResponseEntity<?> uploadAvatar(@PathVariable Long accountId,
                                          @RequestParam MultipartFile file) {
        String fileName = fileService.saveOrReplaceAvatar(accountId, file);
        String url = "%s/api/profiles/%d/avatar".formatted(serverUrl, accountId);
        System.out.println(fileName);
        return ResponseEntity.ok(Map.of(
                "fileName", fileName,
                "url", url
        ));
    }

    // Xem avatar (PUBLIC)
//    @GetMapping
//    public ResponseEntity<Resource> viewAvatar(@PathVariable Long accountId) throws Exception {
//        Resource res = fileService.loadAvatar(accountId);
//        String ct = Files.probeContentType(res.getFile().toPath());
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + res.getFilename() + "\"")
//                .header(HttpHeaders.CONTENT_TYPE, ct != null ? ct : "application/octet-stream")
//                .body(res);
//    }

    // Xoá avatar
    @DeleteMapping
    public ResponseEntity<?> deleteAvatar(@PathVariable Long accountId) {
        boolean deleted = fileService.deleteAvatar(accountId);
        return ResponseEntity.status(deleted ? 200 : 404)
                .body(Map.of("deleted", deleted));
    }
}
