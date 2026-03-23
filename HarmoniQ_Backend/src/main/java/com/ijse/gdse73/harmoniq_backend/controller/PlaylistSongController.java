package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.PlaylistSongDTO;
import com.ijse.gdse73.harmoniq_backend.service.PlaylistSongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/playlist-song")
@CrossOrigin
@RequiredArgsConstructor
public class PlaylistSongController {
    private final PlaylistSongService playlistSongService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<APIResponse> addNewPlaylistSong(@RequestBody PlaylistSongDTO playlistSongDTO) {
        playlistSongService.addNewPlaylistSong(playlistSongDTO);

        return ResponseEntity.ok(new APIResponse(
                200,"OK",null
        ));
    }

    @GetMapping("/get-all/{playlist_id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<APIResponse> getAllPlaylistSongs(@PathVariable Long playlist_id) {
        return ResponseEntity.ok(new APIResponse(
                200,"OK",playlistSongService.getAllPlaylistSongs(playlist_id)
        ));
    }

    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<APIResponse> removeSongFromPlaylist(@RequestBody PlaylistSongDTO playlistSongDTO) {
        playlistSongService.removePlaylistSong(playlistSongDTO);

        return ResponseEntity.ok(new APIResponse(
                 200,"OK",null
        ));
    }
}
