
package org.Technical.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final RetrievalService retrievalService;
    private final SyncMcpToolCallbackProvider mcpToolCallbackProvider;

    private static final String SYSTEM_PROMPT = """
            You are the SnapShip Engineering Knowledge Assistant.

            You can use two sources of information:

            1. SnapShip documentation context supplied with the question.
            2. MCP tools that provide historical incident information
               and service status.

            Rules:
            1. For documentation-related questions, answer using ONLY
               the supplied SnapShip documentation context.
            2. For questions about historical incidents, use the
               searchIncidents MCP tool when appropriate.
            3. For questions about service status, use the
               getServiceStatus MCP tool when appropriate.
            4. You may combine documentation and MCP tool results
               when the question requires both.
            5. Do not invent technical details or tool results.
            6. If documentation does not contain the required answer,
               respond:
               I could not find this in the SnapShip docs.
            7. Cite the page title and heading when using documentation.
            8. Clearly distinguish information from documentation
               and information returned by MCP tools.
            9. Keep the answer clear and practical.
            """;

    public ChatService(
            ChatClient.Builder builder,
            RetrievalService retrievalService,
            SyncMcpToolCallbackProvider mcpToolCallbackProvider) {

        this.chatClient = builder.build();
        this.retrievalService = retrievalService;
        this.mcpToolCallbackProvider = mcpToolCallbackProvider;
    }

    public String chat(String question) {

        List<Document> chunks = retrievalService.retrieve(question);

        StringBuilder context = new StringBuilder();

        for (Document chunk : chunks) {

            context.append("Page Title: ")
                    .append(chunk.getMetadata().get("page_title"))
                    .append("\n");

            context.append("Heading: ")
                    .append(chunk.getMetadata().get("heading"))
                    .append("\n");

            context.append("Content:\n")
                    .append(chunk.getText())
                    .append("\n\n---\n\n");
        }

        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .tools(mcpToolCallbackProvider)
                .user("""
                        SnapShip documentation context:

                        %s

                        User question:
                        %s
                        """.formatted(context, question))
                .call()
                .content();
    }
}
