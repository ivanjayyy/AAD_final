//package com.ijse.gdse73.harmoniq_backend.service.ai;
//
//import com.ijse.gdse73.harmoniq_backend.repo.EmbeddingRepo;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class RagService {
//
//    private final EmbeddingService embeddingService;
//    private final EmbeddingRepo embeddingRepository;
//    private final GroqService groqService;
//
//    public String chat(String question) throws Exception {
//
//        List<String> contextList = embeddingService.search(question);
//
//        String context = String.join("\n", contextList);
//
//        String prompt = """
//        Answer ONLY using the context below.
//
//        Context:
//        %s
//
//        Question:
//        %s
//        """.formatted(context, question);
//
//        return groqService.ask(prompt);
//    }
//
//
//}
