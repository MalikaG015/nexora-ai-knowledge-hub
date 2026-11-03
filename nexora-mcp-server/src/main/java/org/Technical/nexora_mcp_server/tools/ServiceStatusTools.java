
package org.Technical.nexora_mcp_server.tools;

import org.springframework.stereotype.Component;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;

import java.util.Locale;
import java.util.Map;

@Component
public class ServiceStatusTools {

    private final Map<String, String> serviceStatuses = Map.of(
            "payment-service", "UP",
            "login-service", "UP",
            "order-service", "DEGRADED"
    );

    @McpTool(
            name = "getServiceStatus",
            description = "Get the status of a technical service. "
                    + "Use this tool when you need to check whether a service "
                    + "is UP, DEGRADED or UNKNOWN."
    )
    public String getServiceStatus(
            @McpToolParam(
                    description = "Service name, such as payment-service, "
                            + "login-service or order-service"
            )
            String serviceName
    ) {

        if (serviceName == null || serviceName.isBlank()) {
            return "UNKNOWN";
        }

        return serviceStatuses.getOrDefault(
                serviceName.toLowerCase(Locale.ROOT).trim(),
                "UNKNOWN"
        );
    }
}
