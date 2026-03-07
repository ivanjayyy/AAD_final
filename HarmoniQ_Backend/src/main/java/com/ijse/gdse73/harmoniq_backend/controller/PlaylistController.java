package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.PlaylistDTO;
import com.ijse.gdse73.harmoniq_backend.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/playlist")
@CrossOrigin
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<APIResponse> addPlaylist(@RequestBody PlaylistDTO playlistDTO){
        playlistService.createPlaylist(playlistDTO);
        return ResponseEntity.ok(new APIResponse(
                200,"OK",null
        ));
    }
}
