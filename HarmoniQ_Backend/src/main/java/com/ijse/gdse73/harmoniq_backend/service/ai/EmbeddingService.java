//package com.ijse.gdse73.harmoniq_backend.service.ai;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ijse.gdse73.harmoniq_backend.entity.Embedding;
//import com.ijse.gdse73.harmoniq_backend.repo.EmbeddingRepo;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
//@Service
//@RequiredArgsConstructor
//public class EmbeddingService {
//    private final EmbeddingRepo embeddingRepo;
//    private final EmbeddingService embeddingService;
//
//    @Value("${hf.api.key}")
//    private String apiKey;
//
//    @Value("${hf.api.url}")
//    private String apiUrl;
//
//    private final WebClient webClient = WebClient.builder().build();
//
//    public List<Double> createEmbedding(String text) {
//
//        List response = webClient.post()
//                .uri(apiUrl)
//                .header("Authorization", "Bearer " + apiKey)
//                .bodyValue(text)
//                .retrieve()
//                .bodyToMono(List.class)
//                .block();
//
//        return (List<Double>) response.get(0);
//    }
//
//    public void indexSong(String songData) throws JsonProcessingException {
//
//        List<Double> vector = embeddingService.createEmbedding(songData);
//
//        Embedding embedding = new Embedding();
//        embedding.setContent(songData);
//        embedding.setVector(new ObjectMapper().writeValueAsString(vector));
//
//        embeddingRepo.save(embedding);
//    }
//
//    public double cosineSimilarity(List<Double> v1, List<Double> v2) {
//        double dot = 0, normA = 0, normB = 0;
//
//        for (int i = 0; i < v1.size(); i++) {
//            dot += v1.get(i) * v2.get(i);
//            normA += v1.get(i) * v1.get(i);
//            normB += v2.get(i) * v2.get(i);
//        }
//
//        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
//    }
//
//    public List<String> search(String query) throws Exception {
//
//        List<Double> queryVector = embeddingService.createEmbedding(query);
//
//        List<Embedding> all = embeddingRepo.findAll();
//
//        return all.stream()
//                .map(e -> {
//                    try {
//                        List<Double> vec = new ObjectMapper()
//                                .readValue(e.getVector(), List.class);
//
//                        double score = cosineSimilarity(queryVector, vec);
//
//                        return Map.entry(e.getContent(), score);
//
//                    } catch (Exception ex) {
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
//                .limit(5)
//                .map(Map.Entry::getKey)
//                .toList();
//    }
//}
