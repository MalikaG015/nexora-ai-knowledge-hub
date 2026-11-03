package org.Technical.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EvaluationService {
    private final RetrievalService retrievalService;
    private final ObjectMapper objectMapper=new ObjectMapper();
    private final ChatService chatService;

    public EvaluationService(
            RetrievalService retrievalService,ChatService chatService) {
        this.retrievalService = retrievalService;
        this.chatService=chatService;

    }
    public void evaluate() throws Exception{
        // Step 1: Load test questions from testset.json
        InputStream inputStream =
                new ClassPathResource("evaluation/testset.json")
                        .getInputStream();

        List<EvaluationQuestion> questions =
                objectMapper.readValue(
                        inputStream,
                        new TypeReference<List<EvaluationQuestion>>() {}
                );

        int evaluatedQuestions = 0;
        int questionsWithHit = 0;

        double totalRecall = 0.0;
        double totalPrecision = 0.0;
        double totalReciprocalRank = 0.0;

        System.out.println("\n========== RAG EVALUATION ==========");

        // Step 2: Evaluate each answerable question
        for (EvaluationQuestion question : questions) {

            if (!question.isAnswerable()) {
                continue;
            }

            evaluatedQuestions++;

            // Expected IDs: our manually labelled ground truth
            Set<String> expectedIds =
                    new HashSet<>(question.getExpectedChunkIds());

            // Retrieve the top 5 chunks from Pinecone
            List<Document> retrievedDocuments =
                    retrievalService.retrieve(question.getQuestion());

            int relevantRetrieved = 0;
            int firstRelevantRank = 0;

            // Step 3: Compare expected IDs with retrieved IDs
            for (int i = 0; i < retrievedDocuments.size(); i++) {

                Document document = retrievedDocuments.get(i);
                String retrievedId = document.getId();

                if (expectedIds.contains(retrievedId)) {

                    relevantRetrieved++;

                    // Rank starts from 1, not 0
                    if (firstRelevantRank == 0) {
                        firstRelevantRank = i + 1;
                    }
                }
            }

            // Metric 1: Hit Rate@5
            boolean hit = relevantRetrieved > 0;

            if (hit) {
                questionsWithHit++;
            }

            // Metric 2: Recall@5
            double recall = expectedIds.isEmpty()
                    ? 0.0
                    : (double) relevantRetrieved / expectedIds.size();

            // Metric 3: Precision@5
            double precision = retrievedDocuments.isEmpty()
                    ? 0.0
                    : (double) relevantRetrieved / retrievedDocuments.size();

            // Metric 4: Reciprocal Rank
            double reciprocalRank = firstRelevantRank == 0
                    ? 0.0
                    : 1.0 / firstRelevantRank;

            totalRecall += recall;
            totalPrecision += precision;
            totalReciprocalRank += reciprocalRank;

            System.out.printf(
                    "%s | Hit=%b | Recall=%.2f | Precision=%.2f | RR=%.2f%n",
                    question.getId(),
                    hit,
                    recall,
                    precision,
                    reciprocalRank
            );
        }

        // Step 4: Calculate average metrics
        if (evaluatedQuestions == 0) {
            System.out.println("No answerable questions found.");
            return;
        }

        double hitRate =
                (double) questionsWithHit / evaluatedQuestions;

        double averageRecall =
                totalRecall / evaluatedQuestions;

        double averagePrecision =
                totalPrecision / evaluatedQuestions;

        double mrr =
                totalReciprocalRank / evaluatedQuestions;

        System.out.println("\n========== FINAL RESULTS ==========");
        System.out.printf("Answerable questions: %d%n", evaluatedQuestions);
        System.out.printf("Hit Rate@5: %.2f%n", hitRate);
        System.out.printf("Average Recall@5: %.2f%n", averageRecall);
        System.out.printf("Average Precision@5: %.2f%n", averagePrecision);
        System.out.printf("MRR: %.2f%n", mrr);
        System.out.println("===================================\n");
    }

    // Represents one question from testset.json
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EvaluationQuestion {

        private String id;
        private String question;
        private String type;
        private List<String> expectedChunkIds;
        private boolean answerable;

        public EvaluationQuestion() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getExpectedChunkIds() {
            return expectedChunkIds;
        }

        public void setExpectedChunkIds(List<String> expectedChunkIds) {
            this.expectedChunkIds = expectedChunkIds;
        }

        public boolean isAnswerable() {
            return answerable;
        }

        public void setAnswerable(boolean answerable) {
            this.answerable = answerable;
        }

    }


    public void evaluateUnanswerable() throws Exception {

        InputStream inputStream =
                new ClassPathResource("evaluation/testset.json")
                        .getInputStream();

        List<EvaluationQuestion> questions =
                objectMapper.readValue(
                        inputStream,
                        new TypeReference<List<EvaluationQuestion>>() {}
                );

        String expectedRefusal =
                "I could not find this in the SnapShip docs.";

        int totalUnanswerable = 0;
        int correctRefusals = 0;

        System.out.println("\n========== REFUSAL EVALUATION ==========");

        for (EvaluationQuestion question : questions) {

            // Evaluate only questions that cannot be answered
            if (question.isAnswerable()) {
                continue;
            }

            totalUnanswerable++;

            // Send the question through the actual chatbot
            String response = chatService.chat(question.getQuestion());

            boolean refusedCorrectly =
                    response != null &&
                            response.trim().equals(expectedRefusal);

            if (refusedCorrectly) {
                correctRefusals++;
            }

            System.out.printf(
                    "%s | Refused correctly=%b | Response=%s%n",
                    question.getId(),
                    refusedCorrectly,
                    response
            );
        }

        double refusalAccuracy = totalUnanswerable == 0
                ? 0.0
                : (double) correctRefusals / totalUnanswerable;

        System.out.println("\n========== REFUSAL RESULTS ==========");
        System.out.printf(
                "Unanswerable questions: %d%n",
                totalUnanswerable
        );
        System.out.printf(
                "Correct refusals: %d%n",
                correctRefusals
        );
        System.out.printf(
                "Refusal accuracy: %.2f%n",
                refusalAccuracy
        );
        System.out.println("======================================\n");
    }

}
