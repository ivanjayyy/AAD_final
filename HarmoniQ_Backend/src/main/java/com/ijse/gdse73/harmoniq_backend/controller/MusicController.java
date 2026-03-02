package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.service.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/music")
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

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mp4")) // M4A = audio/mp4
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + musicDTO.getFileName() + "\"")
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
}