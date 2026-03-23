package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.ArtistDTO;
import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Artist;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("api/v1/artist")
@CrossOrigin
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;
    private final String artistDir = System.getProperty("user.dir") + "/uploads/artistProfile/";

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse> addArtist(@RequestParam("name") String name,
                                                 @RequestParam("bio") String bio,
                                                 @RequestParam("profilePic") MultipartFile profilePic) throws IOException {

        String artistName = profilePic.getOriginalFilename();
        File artistProfileDirectory = new File(artistDir);

        if (!artistProfileDirectory.exists()) {
            artistProfileDirectory.mkdirs();
        }
        Path artistProfilePath = Paths.get(artistDir + artistName);
        Files.write(artistProfilePath, profilePic.getBytes());

        ArtistDTO artistDTO = new ArtistDTO();
        artistDTO.setName(name);
        artistDTO.setBio(bio);
        artistDTO.setPfpPath("/uploads/artistProfile/" + artistName);

        artistService.addArtist(artistDTO);

        return ResponseEntity.ok(new APIResponse(
                200,"OK",null
        ));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse> deleteArtist(@PathVariable Long id) {
        Artist artist = artistService.deleteArtist(id);

        String artistProfileName = new File(artist.getPfpPath()).getName();
        File artistProfile = new File(artistDir + artistProfileName);
        if (artistProfile.exists()) {
            artistProfile.delete();
        }

        return ResponseEntity.ok(new APIResponse(
                 200,"OK",null
        ));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse> updateArtist(@PathVariable Long id,
                                                    @RequestParam("name") String name,
                                                    @RequestParam("bio") String bio,
                                                    @RequestParam(value = "profilePic", required = false) MultipartFile profilePic) throws IOException {

        ArtistDTO existingDTO = artistService.findArtist(id);

        ArtistDTO updatedDTO = new ArtistDTO();
        updatedDTO.setId(id);
        updatedDTO.setName(name);
        updatedDTO.setBio(bio);
        updatedDTO.setPfpPath(existingDTO.getPfpPath());

        if (profilePic != null && !profilePic.isEmpty()) {
            String oldArtistProfileName = new File(existingDTO.getPfpPath()).getName();
            Files.deleteIfExists(Paths.get(artistDir + oldArtistProfileName));

            String newArtistProfileName = profilePic.getOriginalFilename();
            Files.write(Paths.get(artistDir + newArtistProfileName), profilePic.getBytes());
            updatedDTO.setPfpPath("/uploads/artistProfile/" + newArtistProfileName);
        }

        artistService.updateArtist(updatedDTO);
        return ResponseEntity.ok(new APIResponse(
                 200,"OK",null
        ));
    }

    @GetMapping("/find/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<APIResponse> findArtist(@PathVariable Long id) {
        return ResponseEntity.ok(new APIResponse(
                 200,"OK",artistService.findArtist(id)
        ));
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<APIResponse> getAllArtists() {
        return ResponseEntity.ok(new APIResponse(
                200,"OK",artistService.getAllArtists()
        ));
    }

    @GetMapping("/profile-pic/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Resource> getProfilePic(@PathVariable Long id) {

        try {
            // 1. Get music metadata
            ArtistDTO artistDTO = artistService.findArtist(id);

            if (artistDTO == null || artistDTO.getPfpPath() == null) {
                throw new CustomException("Profile Picture not found");
            }

            // 2. Resolve thumbnail file path
            String artistProfileName = new File(artistDTO.getPfpPath()).getName();
            Path artistProfilePath = Paths.get(artistDir + artistProfileName);

            Resource resource = new UrlResource(artistProfilePath.toUri());

            if (!resource.exists()) {
                throw new CustomException("Profile Picture not found");
            }

            // 3. Return as image resource
            String contentType = Files.probeContentType(artistProfilePath);
            if (contentType == null) {
                contentType = "image/jpeg"; // default
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
