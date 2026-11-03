package org.Technical.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.document.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class ChunkInventoryRunner implements CommandLineRunner {
    private final DocumentLoaderService loaderService;
    private final DocumentChunkingService chunkingService;
    private final ObjectMapper objectMapper=new ObjectMapper();;

    public ChunkInventoryRunner(
            DocumentLoaderService loaderService,
            DocumentChunkingService chunkingService) {
        this.loaderService = loaderService;
        this.chunkingService = chunkingService;

    }

    @Override
    public void run(String... args) throws Exception {

        List<Map<String, Object>> inventory = new ArrayList<>();

        for (Map.Entry<String, String> entry :
                loaderService.loadDocuments().entrySet()) {

            String filename = entry.getKey();

            List<Document> chunks =
                    chunkingService.chunkDocument(
                            filename, entry.getValue());

            for (int i = 0; i < chunks.size(); i++) {

                Document chunk = chunks.get(i);

                String uniqueKey =
                        filename + "|" + i + "|" + chunk.getText();

                String chunkId = UUID.nameUUIDFromBytes(
                        uniqueKey.getBytes(StandardCharsets.UTF_8)
                ).toString();

                Map<String, Object> row = new LinkedHashMap<>();

                row.put("chunk_id", chunkId);
                row.put("source", filename);
                row.put("page_title",
                        chunk.getMetadata().get("page_title"));
                row.put("heading",
                        chunk.getMetadata().get("heading"));
                row.put("subheading",
                        chunk.getMetadata().get("subheading"));
                row.put("text", chunk.getText());

                inventory.add(row);
            }
        }

        System.out.println("TOTAL INVENTORY: " + inventory.size());

        for (Map<String, Object> row : inventory) {
            System.out.println(objectMapper.writeValueAsString(row));
        }
    }
}
