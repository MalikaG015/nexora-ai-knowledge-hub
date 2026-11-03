package org.Technical.service;

import org.Technical.entity.MarkdownSection;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentChunkingService {
    private final MarkdownSectionService markdownSectionService;

    public DocumentChunkingService(MarkdownSectionService markdownSectionService){
        this.markdownSectionService=markdownSectionService;
    }
    public List<Document> chunkDocument(String filename, String content) {
        List<MarkdownSection> sections =
                markdownSectionService.extractSections(content);

        TokenTextSplitter splitter =
                TokenTextSplitter.builder()
                        .withChunkSize(300)
                        .build();

        List<Document> allChunks = new ArrayList<>();

        for (MarkdownSection section : sections) {

            StringBuilder textBuilder = new StringBuilder();

            textBuilder.append("Document: ")
                    .append(section.getPageTitle())
                    .append("\n");

            if (!section.getHeading().isBlank()) {
                textBuilder.append("Heading: ")
                        .append(section.getHeading())
                        .append("\n");
            }

            if (!section.getSubheading().isBlank()) {
                textBuilder.append("Subheading: ")
                        .append(section.getSubheading())
                        .append("\n");
            }

            textBuilder.append("\n")
                    .append(section.getContent());

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("page_title", section.getPageTitle());
            metadata.put("heading", section.getHeading());
            metadata.put("subheading", section.getSubheading());
            metadata.put("source", filename);

            Document sectionDocument =
                    new Document(textBuilder.toString(), metadata);

            List<Document> sectionChunks =
                    splitter.split(List.of(sectionDocument));

            for (Document chunk : sectionChunks) {
                Map<String, Object> chunkMetadata =
                        new HashMap<>(metadata);

                chunkMetadata.putAll(chunk.getMetadata());

                Document finalChunk =
                        new Document(chunk.getText(), chunkMetadata);

                allChunks.add(finalChunk);
            }
        }


        return allChunks;
    }
}
