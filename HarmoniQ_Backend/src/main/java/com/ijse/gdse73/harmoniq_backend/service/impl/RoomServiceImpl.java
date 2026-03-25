//package com.ijse.gdse73.harmoniq_backend.service.impl;
//
//import com.ijse.gdse73.harmoniq_backend.dto.PlayerAction;
//import com.ijse.gdse73.harmoniq_backend.entity.Room;
//import com.ijse.gdse73.harmoniq_backend.repo.RoomRepo;
//import com.ijse.gdse73.harmoniq_backend.service.RoomService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class RoomServiceImpl implements RoomService {
//
//    private final RoomRepo roomRepo;
//
//    public Room createRoom(Long userId) {
//        Room room = new Room();
//        room.setRoomCode(generateCode());
//        room.setHostUserId(userId);
//        room.setPlaying(false);
//        return roomRepo.save(room);
//    }
//
//    public Room joinRoom(String code) {
//        return roomRepo.findByRoomCode(code)
//                .orElseThrow(() -> new RuntimeException("Room not found"));
//    }
//
//    public Room updateState(PlayerAction action) {
//        Room room = roomRepo.findByRoomCode(action.getRoomId())
//                .orElseThrow();
//
//        room.setCurrentSongId(action.getSongId());
//        room.setCurrentTimestamp(action.getTimestamp());
//        room.setPlaying("PLAY".equals(action.getAction()));
//
//        return roomRepo.save(room);
//    }
//
//    private String generateCode() {
//        return UUID.randomUUID().toString().substring(0, 6);
//    }
//}
