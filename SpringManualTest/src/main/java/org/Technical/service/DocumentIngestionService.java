package org.Technical.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class DocumentIngestionService {
    private final DocumentLoaderService documentLoaderService;
    private final DocumentChunkingService documentChunkingService;
    private final VectorStore vectorStore;

    public DocumentIngestionService(
            DocumentLoaderService documentLoaderService,
            DocumentChunkingService documentChunkingService,
            VectorStore vectorStore) {

        this.documentLoaderService = documentLoaderService;
        this.documentChunkingService = documentChunkingService;
        this.vectorStore = vectorStore;
    }
    public int ingestDocuments() throws Exception{
        Map<String, String> documents =
                documentLoaderService.loadDocuments();

        List<Document> allChunks = new ArrayList<>();

        for (Map.Entry<String, String> entry : documents.entrySet()) {

            String filename = entry.getKey();
            String content = entry.getValue();

            List<Document> chunks =
                    documentChunkingService.chunkDocument(filename, content);

            for (int i = 0; i < chunks.size(); i++) {

                Document chunk = chunks.get(i);

                // Stable ID: the same source and chunk get the same ID
                String uniqueKey =
                        filename + "|" + i + "|" + chunk.getText();

                String chunkId = UUID.nameUUIDFromBytes(
                        uniqueKey.getBytes(StandardCharsets.UTF_8)
                ).toString();

                Map<String, Object> metadata =
                        new HashMap<>(chunk.getMetadata());

                metadata.put("source", filename);
                metadata.put("chunk_id", chunkId);

                Document storedChunk = new Document(
                        chunkId,
                        chunk.getText(),
                        metadata
                );

                allChunks.add(storedChunk);
            }
        }

        if (!allChunks.isEmpty()) {
            vectorStore.add(allChunks);
        }

        System.out.println("Documents loaded: " + documents.size());
        System.out.println("Chunks sent to Pinecone: " + allChunks.size());

        return allChunks.size();
    }

}
