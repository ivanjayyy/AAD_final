package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Music;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.service.MusicService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/music")
@CrossOrigin
@RequiredArgsConstructor
public class MusicController {
    private final MusicService musicService;
    private final String musicDir = System.getProperty("user.dir") + "/uploads/music/";
    private final String thumbnailDir = System.getProperty("user.dir") + "/uploads/thumbnail/";

    @PostMapping("/upload")
    public ResponseEntity<APIResponse> uploadMusic(@RequestParam("musicFile") MultipartFile musicFile,
                                                   @RequestParam("thumbnail") MultipartFile thumbnail,
                                                   @RequestParam("musicTitle") String musicTitle,
                                                   @RequestParam("musicArtist") String musicArtist) throws IOException {

//        Music File
        String musicName = musicFile.getOriginalFilename();
        File musicDirectory = new File(musicDir);

        if (!musicDirectory.exists()) {
            musicDirectory.mkdirs();
        }
        Path musicPath = Paths.get(musicDir + musicName);
        Files.write(musicPath, musicFile.getBytes());

//        Thumbnail File
        String thumbnailName = thumbnail.getOriginalFilename();
        File thumbnailDirectory = new File(thumbnailDir);

        if (!thumbnailDirectory.exists()) {
            thumbnailDirectory.mkdirs();
        }
        Path thumbnailPath = Paths.get(thumbnailDir + thumbnailName);
        Files.write(thumbnailPath, thumbnail.getBytes());

//        Save to DTO
        MusicDTO musicDTO = new MusicDTO();
        musicDTO.setFileName(musicName);
        musicDTO.setMusicPath("/uploads/music/" + musicName);
        musicDTO.setThumbnailPath("/uploads/thumbnail/" + thumbnailName);
        musicDTO.setMusicTitle(musicTitle);
        musicDTO.setMusicArtist(musicArtist);

        musicService.saveMusic(musicDTO);

        return ResponseEntity.ok(new APIResponse(
                200,"OK",null
        ));
    }

    @GetMapping("/stream/{id}")
    public ResponseEntity<Resource> streamMusic(@PathVariable Long id) {

        try {
            MusicDTO musicDTO = musicService.getMusicById(id);

            Path filePath = Paths.get(musicDir + musicDTO.getFileName());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new CustomException("File not found");
            }

            String fileName = musicDTO.getFileName();
            String encodedFileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mp4"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename*=UTF-8''" + encodedFileName) // Use filename* for UTF-8
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<APIResponse> getAllMusic() {
        return ResponseEntity.ok(new APIResponse(
                200,"OK",musicService.getAllMusic()
        ));
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<APIResponse> deleteMusic(@PathVariable Long id) {
        Music music = musicService.deleteMusic(id);

        // Delete music file
        File musicFile = new File(musicDir + music.getFileName());
        if (musicFile.exists()) {
            musicFile.delete();
        }

        // Delete thumbnail file
        String thumbnailName = new File(music.getThumbnailPath()).getName();
        File thumbnailFile = new File(thumbnailDir + thumbnailName);
        if (thumbnailFile.exists()) {
            thumbnailFile.delete();
        }

        return ResponseEntity.ok(new APIResponse(
                200,"OK",null
        ));
    }

    @PutMapping("/update/{id}")
    @Transactional
    public ResponseEntity<APIResponse> updateMusic(
            @PathVariable Long id,
            @RequestParam(value = "musicFile", required = false) MultipartFile musicFile,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam("musicTitle") String musicTitle,
            @RequestParam("musicArtist") String musicArtist) throws IOException {

        // 1. Fetch existing music metadata
        MusicDTO existingMusic = musicService.getMusicById(id);

        // 2. Prepare new DTO with existing data as default
        MusicDTO updatedDTO = new MusicDTO();
        updatedDTO.setId(id);
        updatedDTO.setMusicTitle(musicTitle);
        updatedDTO.setMusicArtist(musicArtist);
        updatedDTO.setFileName(existingMusic.getFileName());
        updatedDTO.setMusicPath(existingMusic.getMusicPath());
        updatedDTO.setThumbnailPath(existingMusic.getThumbnailPath());

        // 3. Handle Music File Update
        if (musicFile != null && !musicFile.isEmpty()) {
            // Delete old file
            Files.deleteIfExists(Paths.get(musicDir + existingMusic.getFileName()));

            // Save new file
            String newMusicName = musicFile.getOriginalFilename();
            Files.write(Paths.get(musicDir + newMusicName), musicFile.getBytes());

            updatedDTO.setFileName(newMusicName);
            updatedDTO.setMusicPath("/uploads/music/" + newMusicName);
        }

        // 4. Handle Thumbnail File Update
        if (thumbnail != null && !thumbnail.isEmpty()) {
            // Delete old thumbnail
            String oldThumbnailName = new File(existingMusic.getThumbnailPath()).getName();
            Files.deleteIfExists(Paths.get(thumbnailDir + oldThumbnailName));

            // Save new thumbnail
            String newThumbnailName = thumbnail.getOriginalFilename();
            Files.write(Paths.get(thumbnailDir + newThumbnailName), thumbnail.getBytes());

            updatedDTO.setThumbnailPath("/uploads/thumbnail/" + newThumbnailName);
        }

        // 5. Update Database Record
        musicService.updateMusic(updatedDTO);

        return ResponseEntity.ok(new APIResponse(
                200, "Music updated successfully", null
        ));
    }

    @GetMapping("/thumbnail/{id}")
    public ResponseEntity<Resource> getThumbnail(@PathVariable Long id) {

        try {
            // 1. Get music metadata
            MusicDTO musicDTO = musicService.getMusicById(id);

            if (musicDTO == null || musicDTO.getThumbnailPath() == null) {
                throw new CustomException("Thumbnail not found");
            }

            // 2. Resolve thumbnail file path
            String thumbnailName = new File(musicDTO.getThumbnailPath()).getName();
            Path thumbnailPath = Paths.get(thumbnailDir + thumbnailName);

            Resource resource = new UrlResource(thumbnailPath.toUri());

            if (!resource.exists()) {
                throw new CustomException("Thumbnail file not found");
            }

            // 3. Return as image resource
            String contentType = Files.probeContentType(thumbnailPath);
            if (contentType == null) {
                contentType = "image/jpeg"; // default
            }

            // ... inside getThumbnail
            String fileName = resource.getFilename();
            String encodedFileName = UriUtils.encode(fileName != null ? fileName : "thumbnail", StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename*=UTF-8''" + encodedFileName)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}