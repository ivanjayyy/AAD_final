package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.LikedOrRecentSongDTO;
import com.ijse.gdse73.harmoniq_backend.service.RecentSongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/recent-song")
@CrossOrigin
@RequiredArgsConstructor
public class RecentSongController {
    private final RecentSongService recentSongService;

    @PostMapping("/add")
    public ResponseEntity<APIResponse> addRecentSong(@RequestBody LikedOrRecentSongDTO likedOrRecentSongDTO){
        recentSongService.addRecentSong(likedOrRecentSongDTO);
        return ResponseEntity.ok(new APIResponse(
                 200,"Added Recent Song",null
        ));
    }

    @GetMapping("/load-all/{id}")
    public ResponseEntity<APIResponse> loadRecentSongs(@PathVariable Long id) {
        return ResponseEntity.ok(new APIResponse(
                200,"Load Recent Songs", recentSongService.loadRecentSongs(id)
        ));
    }

}
