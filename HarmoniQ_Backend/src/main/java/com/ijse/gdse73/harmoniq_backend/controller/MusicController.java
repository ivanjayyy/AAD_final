package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.entity.Music;
import com.ijse.gdse73.harmoniq_backend.repo.MusicRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicRepo musicRepository;

    // absolute path from project root
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/music/";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMusic(@RequestParam("file") MultipartFile file) {

        try {

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // generate unique file name
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            Path filePath = Paths.get(uploadDir + fileName);

            Files.write(filePath, file.getBytes());

            // Save to DB
            Music music = new Music();
            music.setFileName(fileName);
            music.setFilePath("/uploads/music/" + fileName);

            Music savedMusic = musicRepository.save(music);

            return ResponseEntity.ok(savedMusic);

        } catch (Exception e) {
            e.printStackTrace(); // print real error in console
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}