package org.Technical.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RetrievalTestRunner implements CommandLineRunner {
    private final RetrievalService retrievalService;
    public RetrievalTestRunner(RetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    @Override
    public void run(String... args) {
        var results = retrievalService.retrieve(
                "What does error SS-402 mean?"
        );

        System.out.println("Retrieved chunks: " + results.size());

        results.forEach(document ->
                System.out.println(document.getText())
        );
    }
}
