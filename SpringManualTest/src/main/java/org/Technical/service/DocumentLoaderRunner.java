package org.Technical.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class DocumentLoaderRunner implements CommandLineRunner {
    private final DocumentIngestionService documentIngestionService;

    public DocumentLoaderRunner(DocumentIngestionService documentIngestionService) {
        this.documentIngestionService=documentIngestionService;
    }

    @Override
    public void run(String... args) throws Exception {
        documentIngestionService.ingestDocuments();


    }
}
