package org.Technical.service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DocumentLoaderService {
    public Map<String, String> loadDocuments() throws IOException {
        Map<String, String> documents = new LinkedHashMap<>();
        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver();

        Resource[] resources =
                resolver.getResources("classpath*:documents/*.md");
        for (Resource resource : resources) {

            String filename = resource.getFilename();

            try (InputStream inputStream = resource.getInputStream()) {

                String content = new String(
                        inputStream.readAllBytes(),
                        StandardCharsets.UTF_8
                );

                documents.put(filename, content);
            }
        }
        return documents;
        }
    }

