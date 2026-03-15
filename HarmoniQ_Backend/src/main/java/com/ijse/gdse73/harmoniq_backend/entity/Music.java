package com.ijse.gdse73.harmoniq_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String musicPath;
    private String thumbnailPath;
    private String musicTitle;
    private String musicArtist;

//    // Music can belong to many playlists
//    @ManyToMany(mappedBy = "musics")
//    private Set<Playlist> playlists;
//
//    // Music liked by many users
//    @ManyToMany(mappedBy = "likedMusic")
//    private Set<User> likedByUsers;
}