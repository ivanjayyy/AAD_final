package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.ArtistDTO;
import com.ijse.gdse73.harmoniq_backend.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/artist")
@CrossOrigin
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;

    @PostMapping("/add")
    public ResponseEntity<APIResponse> addArtist(@RequestBody ArtistDTO artistDTO) {
        artistService.addArtist(artistDTO);
        return ResponseEntity.ok(new APIResponse(
                200,"OK",null
        ));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse> deleteArtist(@PathVariable Long id) {
        artistService.deleteArtist(id);
        return ResponseEntity.ok(new APIResponse(
                 200,"OK",null
        ));
    }

    @PutMapping("/update")
    public ResponseEntity<APIResponse> updateArtist(@RequestBody ArtistDTO artistDTO) {
        artistService.updateArtist(artistDTO);
        return ResponseEntity.ok(new APIResponse(
                 200,"OK",null
        ));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<APIResponse> findArtist(@PathVariable Long id) {
        return ResponseEntity.ok(new APIResponse(
                 200,"OK",artistService.findArtist(id)
        ));
    }
}
