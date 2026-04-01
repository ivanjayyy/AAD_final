package com.ijse.gdse73.harmoniq_backend.repo;

import com.ijse.gdse73.harmoniq_backend.entity.Artist;
import com.ijse.gdse73.harmoniq_backend.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicRepo extends JpaRepository<Music, Long> {
    List<Music> findAllByArtist_Id(Long id);
    List<Music> findAllByArtist(Artist artist);

    Music findByMusicTitle(String musicTitle);

    Music findByMusicPath(String musicPath);

    Music findByThumbnailPath(String thumbnailPath);
}
