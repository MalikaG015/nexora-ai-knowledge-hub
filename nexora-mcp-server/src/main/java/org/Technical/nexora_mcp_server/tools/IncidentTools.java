
package org.Technical.nexora_mcp_server.tools;

import org.springframework.stereotype.Component;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;

import java.util.List;
import java.util.Locale;

@Component
public class IncidentTools {

    private final List<String> incidents = List.of(
            "INC001: Payment API returned 500 errors. "
                    + "Cause: Database connection pool exhausted. "
                    + "Resolution: Increased connection pool size.",

            "INC002: Login API returned 401 errors. "
                    + "Cause: JWT token expired. "
                    + "Resolution: Refresh the authentication token.",

            "INC003: Order API response was slow. "
                    + "Cause: Missing database index. "
                    + "Resolution: Added an index to the orders table."
    );

    @McpTool(
            name = "searchIncidents",
            description = "Search historical technical incidents using a keyword. "
                    + "Use this tool to find similar past incidents, their causes "
                    + "and resolutions."
    )
    public List<String> searchIncidents(
            @McpToolParam(
                    description = "Keyword to search for, such as payment, login or order"
            )
            String keyword
    ) {

        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        String searchKeyword = keyword.toLowerCase(Locale.ROOT);

        return incidents.stream()
                .filter(incident ->
                        incident.toLowerCase(Locale.ROOT)
                                .contains(searchKeyword))
                .toList();
    }
}
