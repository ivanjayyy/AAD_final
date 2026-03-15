package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.LikedSongDTO;
import com.ijse.gdse73.harmoniq_backend.dto.PlaylistDTO;
import com.ijse.gdse73.harmoniq_backend.service.LikedSongService;
import com.ijse.gdse73.harmoniq_backend.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/liked-song")
@CrossOrigin
@RequiredArgsConstructor
public class LikedSongController {
    private final LikedSongService likedSongService;

    @PostMapping("/add-or-remove")
    public ResponseEntity<APIResponse> addOrRemoveLike(@RequestBody LikedSongDTO likedSongDTO){
        return ResponseEntity.ok(new APIResponse(
                200,"OK",likedSongService.addOrRemoveLike(likedSongDTO)
        ));
    }

    @GetMapping("/get-by-user/{id}")
    public ResponseEntity<APIResponse> getLikedSongsByUser(@PathVariable Long id){
        return ResponseEntity.ok(new APIResponse(
                200,"OK",likedSongService.getLikedSongsByUser(id)
        ));
    }
}
