package org.Technical.service;

import org.Technical.entity.MarkdownSection;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarkdownSectionService {
    public List<MarkdownSection> extractSections(String content) {

        List<MarkdownSection> sections = new ArrayList<>();

        String pageTitle = "Untitled Document";
        String heading = "";
        String subheading = "";

        StringBuilder sectionContent = new StringBuilder();

        String[] lines = content.split("\\R");

        for (String line : lines) {

            if (line.startsWith("# ")) {
                pageTitle = line.substring(2).trim();
                continue;
            }

            if (line.startsWith("## ") || line.startsWith("### ")) {

                // Save the previous section before starting a new one
                if (!sectionContent.toString().isBlank()) {
                    sections.add(
                            new MarkdownSection(
                                    pageTitle,
                                    heading,
                                    subheading,
                                    sectionContent.toString().trim()
                            )
                    );
                }

                // Update the heading or subheading
                if (line.startsWith("### ")) {
                    subheading = line.substring(4).trim();
                } else {
                    heading = line.substring(3).trim();
                    subheading = "";
                }

                // Start collecting the new section's content
                sectionContent = new StringBuilder();

            } else {
                sectionContent.append(line).append("\n");
            }
        }

        // Save the final section after the loop finishes
        if (!sectionContent.toString().isBlank()) {
            sections.add(
                    new MarkdownSection(
                            pageTitle,
                            heading,
                            subheading,
                            sectionContent.toString().trim()
                    )
            );
        }

        return sections;
    }
}
