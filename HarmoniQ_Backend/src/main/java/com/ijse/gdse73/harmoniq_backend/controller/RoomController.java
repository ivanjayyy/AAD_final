//package com.ijse.gdse73.harmoniq_backend.controller;
//
//import com.ijse.gdse73.harmoniq_backend.entity.Room;
//import com.ijse.gdse73.harmoniq_backend.service.RoomService;
//import com.ijse.gdse73.harmoniq_backend.service.impl.RoomServiceImpl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/room")
//@RequiredArgsConstructor
//@CrossOrigin
//public class RoomController {
//
//    private final RoomServiceImpl roomService;
//
//    @PostMapping("/create")
//    public Room create(@RequestParam Long userId) {
//        return roomService.createRoom(userId);
//    }
//
//    @GetMapping("/join/{code}")
//    public Room join(@PathVariable String code) {
//        return roomService.joinRoom(code);
//    }
//}
