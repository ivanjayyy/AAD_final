package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.PlaylistDTO;
import com.ijse.gdse73.harmoniq_backend.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/playlist")
@CrossOrigin
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<APIResponse> addPlaylist(@RequestBody PlaylistDTO playlistDTO){
        playlistService.createPlaylist(playlistDTO);
        return ResponseEntity.ok(new APIResponse(
                200,"OK",null
        ));
    }

    @GetMapping("/load/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<APIResponse> getUserPlaylists(@PathVariable Long userId){
        List<PlaylistDTO> playlistDTOS = playlistService.getPlaylistsByUserId(userId);

        return ResponseEntity.ok(new APIResponse(
                 200,"OK",playlistDTOS
        ));
    }

    @PutMapping("/update/{playlistId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<APIResponse> updatePlaylist(@PathVariable String playlistId, @RequestBody PlaylistDTO playlistDTO) {
//        playlistService.updatePlaylist(playlistId, playlistDTO);
        return ResponseEntity.ok(new APIResponse(
                200, "Playlist updated successfully", null
        ));
    }

    @DeleteMapping("/delete/{playlistId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<APIResponse> deletePlaylist(@PathVariable Long playlistId) {
        playlistService.deletePlaylist(playlistId);
        return ResponseEntity.ok(new APIResponse(
                200, "Playlist deleted successfully", null
        ));
    }
}
