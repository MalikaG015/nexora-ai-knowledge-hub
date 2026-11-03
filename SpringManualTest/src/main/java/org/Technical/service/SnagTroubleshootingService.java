
package org.Technical.service;

import org.Technical.dto.SnagRequest;
import org.Technical.dto.SnagResponse;
import org.Technical.exception.DependencyFailureException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SnagTroubleshootingService {

    private final ChatClient chatClient;
    private final RetrievalService retrievalService;
    private final SyncMcpToolCallbackProvider mcpToolCallbackProvider;

    public SnagTroubleshootingService(
            ChatClient.Builder builder,
            RetrievalService retrievalService,
            SyncMcpToolCallbackProvider mcpToolCallbackProvider) {

        this.chatClient = builder.build();
        this.retrievalService = retrievalService;
        this.mcpToolCallbackProvider = mcpToolCallbackProvider;
    }

    public SnagResponse troubleshoot(SnagRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Snag request must not be null"
            );
        }

        String description = request.getDescription();

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Snag description must not be empty"
            );
        }

        // Step 1: Retrieve relevant documentation from Pinecone
        List<Document> chunks;

        try {
            chunks = retrievalService.retrieve(description);
        } catch (Exception ex) {
            throw new DependencyFailureException(
                    "Documentation retrieval failed", ex
            );
        }

        // Step 2: Build documentation context
        StringBuilder context = new StringBuilder();

        if (chunks != null) {
            for (Document chunk : chunks) {

                context.append("Page Title: ")
                        .append(chunk.getMetadata().get("page_title"))
                        .append("\n");

                context.append("Heading: ")
                        .append(chunk.getMetadata().get("heading"))
                        .append("\n");

                context.append("Content:\n")
                        .append(chunk.getText())
                        .append("\n\n");
            }
        }

        if (context.isEmpty()) {
            context.append("No relevant documentation was found.");
        }

        // Step 3: Ask the LLM to analyze the snag using RAG and MCP
        SnagResponse response;

        try {
            response = chatClient.prompt()
                    .system("""
                            You are a technical troubleshooting assistant
                            for the Nexora AI Knowledge Hub.

                            You have access to two information sources:

                            1. SnapShip documentation supplied in the context.
                            2. MCP tools that can search historical incidents
                               and check service status.

                            Analyze the developer's problem using these sources.

                            Return a structured response containing:
                            1. problem
                            2. possibleCauses
                            3. firstCheck
                            4. nextSteps

                            Rules:
                            - Use documentation as the primary source for
                              documented troubleshooting procedures.
                            - Use searchIncidents when historical incidents
                              could help investigate the problem.
                            - Use getServiceStatus when checking a service's
                              current status is relevant.
                            - Combine documentation and MCP results when useful.
                            - Do not invent causes or troubleshooting steps.
                            - Clearly distinguish documented facts from
                              historical incident information and service status.
                            - If documentation is missing, say so clearly.
                            - Do not claim to know the actual root cause
                              unless the available evidence establishes it.
                            - If information is insufficient, explain what
                              additional investigation is required.
                            """)
                    .tools(mcpToolCallbackProvider)
                    .user("""
                            Documentation context:

                            %s

                            Developer's snag:

                            %s
                            """.formatted(context, description))
                    .call()
                    .entity(SnagResponse.class);

        } catch (Exception ex) {
            throw new DependencyFailureException(
                    "Troubleshooting analysis failed", ex
            );
        }

        // Step 4: Validate the LLM response
        if (response == null) {
            throw new DependencyFailureException(
                    "LLM returned an empty response",
                    new IllegalStateException("SnagResponse was null")
            );
        }

        return response;
    }
}
