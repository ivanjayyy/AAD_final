package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.ArtistDTO;
import com.ijse.gdse73.harmoniq_backend.dto.UserDTO;
import com.ijse.gdse73.harmoniq_backend.dto.UserProfilePicDTO;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.service.UserProfilePicService;
import com.ijse.gdse73.harmoniq_backend.service.UserService;
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
import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserProfilePicService userProfilePicService;
    private final String userDir = System.getProperty("user.dir") + "/uploads/userProfile/";

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<APIResponse> getUser(@PathVariable Long id){
        return ResponseEntity.ok(new APIResponse(
                200,"User Fetched Successfully!",userService.getUser(id)
        ));
    }

    @GetMapping("/get-profile-pic/{id}")
    public ResponseEntity<Resource> getProfilePic(@PathVariable Long id){
        try {
            UserProfilePicDTO userProfilePicDTO = userProfilePicService.findProfilePic(id);

            if (userProfilePicDTO == null || userProfilePicDTO.getProfilePic() == null) {
                throw new CustomException("Profile Picture not found");
            }

            // 2. Resolve thumbnail file path
            String userProfileName = new File(userProfilePicDTO.getProfilePic()).getName();
            Path userProfilePath = Paths.get(userDir + userProfileName);

            Resource resource = new UrlResource(userProfilePath.toUri());

            if (!resource.exists()) {
                throw new CustomException("Profile Picture not found");
            }

            // 3. Return as image resource
            String contentType = Files.probeContentType(userProfilePath);
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

    @PostMapping("/upload-profile-pic")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<APIResponse> uploadProfilePic(@RequestParam("userId") Long userId, @RequestParam("profilePic") MultipartFile profilePic) throws IOException {
        String userName = profilePic.getOriginalFilename();
        File userProfileDirectory = new File(userDir);

        if (!userProfileDirectory.exists()) {
            userProfileDirectory.mkdirs();
        }
        Path userProfilePath = Paths.get(userDir + userName);
        Files.write(userProfilePath, profilePic.getBytes());

        UserProfilePicDTO userProfilePicDTO = new UserProfilePicDTO();
        userProfilePicDTO.setUserId(userId);
        userProfilePicDTO.setProfilePic("/uploads/userProfile/" + userName);

        userProfilePicService.uploadProfilePic(userProfilePicDTO);

        return ResponseEntity.ok(new APIResponse(
                200,"OK",null
        ));
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse> getAllUser(){
        return ResponseEntity.ok(new APIResponse(
                200,"Get All Users Successfully!",userService.getAllUsers()));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<APIResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new APIResponse(
                200,"User Deleted Successfully",null
        ));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<APIResponse> updateUser(@RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
        return ResponseEntity.ok(new APIResponse(
                200, "User updated successfully", null
        ));
    }

    @PutMapping("/update-role/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse> updateUserRole(@PathVariable Long id, @RequestBody Map<String,String> body) {
        String role = body.get("role");
        userService.updateUserRole(id, role);
        return ResponseEntity.ok(new APIResponse(
                 200, "User role updated successfully", null
        ));
    }
}
