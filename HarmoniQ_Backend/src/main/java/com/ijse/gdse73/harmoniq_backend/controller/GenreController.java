package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.GenreDTO;
import com.ijse.gdse73.harmoniq_backend.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/genre")
@CrossOrigin
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse> addNewGenre(@RequestBody GenreDTO genreDTO){
        genreService.addNewGenre(genreDTO);

        return ResponseEntity.ok(new APIResponse(
                200,"OK",null
        ));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse> updateGenre(@RequestBody GenreDTO genreDTO , @PathVariable Long id){
        genreService.updateGenre(id, genreDTO);

        return ResponseEntity.ok(new APIResponse(
                200,"OK",null
        ));
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse> getAllGenre(){

        return ResponseEntity.ok(new APIResponse(
                200,"OK",genreService.getAllGenre()
        ));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse> deleteGenre(@PathVariable Long id){

        return ResponseEntity.ok(new APIResponse(
                200,"OK",genreService.deleteGenre(id)
        ));
    }
}
