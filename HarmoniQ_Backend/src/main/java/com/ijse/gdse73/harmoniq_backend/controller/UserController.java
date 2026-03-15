package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.UserDTO;
import com.ijse.gdse73.harmoniq_backend.entity.User;
import com.ijse.gdse73.harmoniq_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("api/v1/user")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final String uploadDir = "uploads/profile/";

    @GetMapping("/get/{username}")
    public ResponseEntity<APIResponse> getUser(@PathVariable String username){
        return ResponseEntity.ok(new APIResponse(
                200,"OK",userService.getUser(username)
        ));
    }

    @GetMapping("/get-all")
    public ResponseEntity<APIResponse> getAllUser(){
        return ResponseEntity.ok(new APIResponse(
                200,"OK",userService.getAllUsers()));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new APIResponse(
                200,"OK",null
        ));
    }

    @PutMapping("/update")
    public ResponseEntity<APIResponse> updateUser(@RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
        return ResponseEntity.ok(new APIResponse(
                200, "User updated successfully", null
        ));
    }
}
