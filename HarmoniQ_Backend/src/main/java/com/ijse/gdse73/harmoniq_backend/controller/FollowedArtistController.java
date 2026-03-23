package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.FollowedArtistDTO;
import com.ijse.gdse73.harmoniq_backend.service.FollowedArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/follow-artist")
@CrossOrigin
@RequiredArgsConstructor
public class FollowedArtistController {
    private final FollowedArtistService followedArtistService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<APIResponse> addFollowedArtist(@RequestBody FollowedArtistDTO followedArtistDTO){

        return ResponseEntity.ok(new APIResponse(
                200,"OK",followedArtistService.addFollowedArtist(followedArtistDTO)
        ));
    }

    @PostMapping("/check")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<APIResponse> checkFollowStatus(@RequestBody FollowedArtistDTO followedArtistDTO){

        return ResponseEntity.ok(new APIResponse(
                200,"OK",followedArtistService.checkFollowStatus(followedArtistDTO)
        ));
    }
}
