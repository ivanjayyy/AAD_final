//package com.ijse.gdse73.harmoniq_backend.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import java.util.Set;
//
//@Entity
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class Artist {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String name;
//    private String bio;
//    private String profileImage;
//
//    // One artist can have many music tracks
//    @OneToMany(mappedBy = "artist")
//    private Set<Music> musics;
//}