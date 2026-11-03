package org.Technical.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EvaluationRunner implements CommandLineRunner {
    private final EvaluationService evaluationService;

    public EvaluationRunner(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @Override
    public void run(String... args) throws Exception {
        evaluationService.evaluate();
        evaluationService.evaluateUnanswerable();
    }
}
