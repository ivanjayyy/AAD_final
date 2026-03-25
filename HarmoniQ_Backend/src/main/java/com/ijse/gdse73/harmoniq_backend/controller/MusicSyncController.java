//package com.ijse.gdse73.harmoniq_backend.controller;
//
//import com.ijse.gdse73.harmoniq_backend.dto.PlayerAction;
//import com.ijse.gdse73.harmoniq_backend.entity.Room;
//import com.ijse.gdse73.harmoniq_backend.service.RoomService;
//import com.ijse.gdse73.harmoniq_backend.service.impl.RoomServiceImpl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//@Controller
//@RequiredArgsConstructor
//public class MusicSyncController {
//
//    private final SimpMessagingTemplate messagingTemplate;
//    private final RoomServiceImpl roomService;
//
//    @MessageMapping("/sync")
//    public void sync(PlayerAction action) {
//
//        // update DB state
//        Room updatedRoom = roomService.updateState(action);
//
//        // broadcast to room
//        messagingTemplate.convertAndSend(
//                "/topic/room/" + action.getRoomId(),
//                updatedRoom
//        );
//    }
//}
