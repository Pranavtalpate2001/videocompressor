package com.example.demo.controller;




import com.example.demo.service.VideoCompressorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
public class VideoController {

    @Autowired
    private VideoCompressorService compressorService;

    @PostMapping("/compress")
    public ResponseEntity<FileSystemResource> compressAndGet(@RequestParam("file") MultipartFile file) {
        try {
            // Upload folder
            String uploadDir = "D:/uploads/";
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) uploadFolder.mkdirs();

            // Save uploaded file
            String originalFilePath = uploadDir + file.getOriginalFilename();
            Path targetPath = Path.of(originalFilePath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Compressed file path
            String compressedFilePath = originalFilePath.replace(".mp4", "_compressed.mp4");

            // Compress video
            String compressedPath = compressorService.compressVideo(originalFilePath, compressedFilePath);
            if (compressedPath == null) return ResponseEntity.status(500).body(null);

            File compressedFile = new File(compressedPath);
            if (!compressedFile.exists()) return ResponseEntity.notFound().build();

            // FileSystemResource
            FileSystemResource resource = new FileSystemResource(compressedFile);

            // Return with Content-Length header for frontend progress
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + compressedFile.getName());
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(compressedFile.length()));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
