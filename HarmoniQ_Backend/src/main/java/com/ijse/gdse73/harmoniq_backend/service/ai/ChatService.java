//package com.ijse.gdse73.harmoniq_backend.service.ai;
//
//import com.ijse.gdse73.harmoniq_backend.entity.*;
//import com.ijse.gdse73.harmoniq_backend.repo.FollowedArtistRepo;
//import com.ijse.gdse73.harmoniq_backend.repo.MusicRepo;
//import com.ijse.gdse73.harmoniq_backend.repo.UserRepo;
//import lombok.RequiredArgsConstructor;
////import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class ChatService {
//
//    private final RestTemplate restTemplate = new RestTemplate();
//    private final MusicRepo musicRepo;
//    private final FollowedArtistRepo followedArtistRepo;
//    private final UserRepo userRepo;
//
//    private String context;
//
//    public void setContext(Long userId) {
//        Long user = Long.parseLong(userId.toString());
//        context = "\n" + buildUserContext(user) + "\n";
//    }
//
//    public String askAI(String msg) {
//        String url = "http://localhost:11434/api/generate";
//
//        String rules =  """
//                            STRICT RULES:
//                            1. ONLY recommend songs that are included in the 'List of all songs with their artist and genre'.
//                            2. DO NOT make up songs.
//                            3. DO NOT suggest anything outside 'List of all songs with their artist and genre'.
//                            4. If the lists are empty, say 'No recommendations available'.
//                            5. If the user asks for a song that is not in the 'List of all songs with their artist and genre', say 'Song not found'.
//                            6. When recommending songs, prioritize 'List of all songs played recently by the user', 'List of all songs liked by the user' and check the song result's Genre and Artist using 'List of all songs with their artist and genre' and also check 'List of all artists followed by the user' and then recommend the most suitable songs.
//                            7. When sending responses, use minimum number of words. For an example when user asks for songs, respond with 'I recommend you to listen to X song by Y artist' etc.
//                            8. When the user asks for songs from a specific Genre, use 'List of all songs with their artist and genre' to fetch few songs from that genre.
//                            9. When the user asks for songs from a specific Artist, use 'List of all songs with their artist and genre' to fetch few songs from that artist.
//
//                            """;
//
//        String prompt = rules +
//                context + "\n\n" +
//                "User: " + msg;
//
//        Map<String, Object> request = new HashMap<>();
//        request.put("model", "llama3");
//        request.put("prompt", prompt);
//        request.put("stream", false);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, Object>> entity =
//                new HttpEntity<>(request, headers);
//
//        ResponseEntity<Map> response =
//                restTemplate.postForEntity(url, entity, Map.class);
//
//        return response.getBody().get("response").toString();
//    }
//
//    public String buildUserContext(Long userId) {
//        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//
//        List<Music> songs = musicRepo.findAll();
//        List<FollowedArtist> artists = followedArtistRepo.findAllByUser(user);
//        List<Music> userLikedSongs = user.getLikedSongs().stream().map(LikedSong::getMusic).toList();
//        List<Music> userRecentSongs = user.getRecentSongs().stream().map(RecentSong::getMusic).toList();
//
//        StringBuilder context = new StringBuilder();
//
//        context.append("List of all songs with their artist and genre: ");
//        for (Music m : songs) {
//            context.append("Song-> '").append(m.getMusicTitle()).append("' by Artist-> '").append(m.getArtist().getName()).append("' of genre-> '").append(m.getGenre().getName()).append("',");
//        }
//
//        context.append("\nList of all artists followed by the user: ");
//        for (FollowedArtist a : artists) {
//            context.append(a.getArtist().getName()).append(",");
//        }
//
//        context.append("\nList of all songs liked by the user: ");
//        for (Music m : userLikedSongs) {
//            context.append(m.getMusicTitle()).append(",");
//        }
//
//        context.append("\nList of all songs played recently by the user: ");
//        for (Music m : userRecentSongs) {
//            context.append(m.getMusicTitle()).append(",");
//        }
//
//        return context.toString();
//    }
//}

package com.ijse.gdse73.harmoniq_backend.service.ai;

import com.ijse.gdse73.harmoniq_backend.entity.*;
import com.ijse.gdse73.harmoniq_backend.repo.FollowedArtistRepo;
import com.ijse.gdse73.harmoniq_backend.repo.MusicRepo;
import com.ijse.gdse73.harmoniq_backend.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final MusicRepo musicRepo;
    private final FollowedArtistRepo followedArtistRepo;
    private final UserRepo userRepo;

    private String contextData;

    public void setContext(Long userId) {
        this.contextData = buildUserContext(userId);
    }

    public String askAI(String msg) {
        String url = "http://localhost:11434/api/generate";

        // Instruction block placed strategically after data
        String prompt = String.format("""
    ### SYSTEM DATABASE
    %s
    
    ### MANDATORY RULES:
    1. INTENT RECOGNITION: 
       - If the user provides a greeting (e.g., "Hi", "Hello"), respond with a friendly greeting and ask how you can help.
       - If the user says "Thank you", respond politely.
       - If the user asks a general question about the system (e.g., "What can you do?"), explain that you provide song recommendations from the database.
       
    2. DATA LIMITATION:
       - ONLY recommend or confirm songs listed in the 'AVAILABLE SONGS' section above.
       - DO NOT use internal knowledge to suggest real-world songs not present in the list.
       
    3. SEARCH/AVAILABILITY REQUESTS:
       - If the user asks "Is there a song called X?", check the list. 
       - If it exists, respond: "Yes, 'X' is available. Would you like me to play it or do you have another request?"
       - If it does NOT exist, respond: "No, 'X' is not in our database. Do you have any other request?"
       
    4. RECOMMENDATION REQUESTS:
       - If the user specifically asks for a recommendation or a genre, use the format: "I recommend you listen to 'Song Title' by 'Artist Name'".
       - If no matches are found for their request, respond exactly: "No recommendations available".

    5. SONG PLAY REQUESTS:
       - If the user wants to play a song, you MUST find the Song ID in the list above.
       - Respond with this exact tag: [ACTION:PLAY_SONG(id_number)]
       - If the song isn't in the list, say "I couldn't find that song."
       
    6. CREATE NEW PLAYLIST REQUESTS:
       - If the user wants to create a new playlist, you MUST check the user Playlist list for duplicates.
       - If there are no duplicates, respond with this exact tag: [ACTION:CREATE_PLAYLIST(playlist_name)]
       - If there are duplicates, say "You already have a playlist with that name."
       
    7. CONCISENESS: Keep all responses brief and focused on the user's specific input.
    
    ### USER REQUEST:
    %s
    
    ### ASSISTANT RESPONSE:
    """, contextData, msg);

        Map<String, Object> request = new HashMap<>();
        request.put("model", "llama3");
        request.put("prompt", prompt);
        request.put("stream", false);

        // Crucial: Set temperature to 0 to stop hallucinations
        Map<String, Object> options = new HashMap<>();
        options.put("temperature", 0.0);
        request.put("options", options);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("response")) {
                return response.getBody().get("response").toString().trim();
            }
            return "AI Error: Empty response";
        } catch (Exception e) {
            return "Error connecting to AI service: " + e.getMessage();
        }
    }

    public String buildUserContext(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Music> allSongs = musicRepo.findAll();
        List<FollowedArtist> followedArtists = followedArtistRepo.findAllByUser(user);
        List<Music> userLikedSongs = user.getLikedSongs().stream().map(LikedSong::getMusic).toList();
        List<Music> userRecentSongs = user.getRecentSongs().stream().map(RecentSong::getMusic).toList();
        List<Playlist> userPlaylists = user.getPlaylists();

        StringBuilder sb = new StringBuilder();

        sb.append("\n[AVAILABLE SONGS]\n");
        for (Music m : allSongs) {
            sb.append(String.format("- Id: '%d' |Title: '%s' | Artist: '%s' | Genre: '%s'\n",
                    m.getId(), m.getMusicTitle(), m.getArtist().getName(), m.getGenre().getName()));
        }

        sb.append("\n[USER PREFERENCES]\n");
        sb.append("- Followed Artists: ").append(
                followedArtists.stream().map(a -> a.getArtist().getName()).collect(Collectors.joining(", "))
        ).append("\n");

        sb.append("- Liked Songs: ").append(
                userLikedSongs.stream().map(Music::getMusicTitle).collect(Collectors.joining(", "))
        ).append("\n");

        sb.append("- Recently Played: ").append(
                userRecentSongs.stream().map(Music::getMusicTitle).collect(Collectors.joining(", "))
        ).append("\n");

        sb.append("\n[USER PLAYLISTS]\n");
        if (userPlaylists.isEmpty()) {
            sb.append("- Playlists: None\n");
        } else {
            sb.append("- Playlists: ").append(
                    userPlaylists.stream().map(Playlist::getPlaylistName).collect(Collectors.joining(", "))
            ).append("\n");
        }

        return sb.toString();
    }
}
