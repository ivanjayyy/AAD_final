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
//public class Genre {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String name;
//
//    // One genre can have many music tracks
//    @OneToMany(mappedBy = "genre")
//    private Set<Music> musics;
//}