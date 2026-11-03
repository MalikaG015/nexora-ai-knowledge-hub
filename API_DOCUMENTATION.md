# API Documentation

## 1. Overview

Nexora AI Knowledge Hub provides REST APIs for interacting with the engineering knowledge assistant and troubleshooting technical issues.

The main Spring Boot application runs on port `8080`.

The project also contains a separate MCP Server running on port `8090`. The MCP Server exposes tools through the Model Context Protocol (MCP), not through ordinary REST endpoints.

## 2. Base URL

```text
http://localhost:8080
```

## 3. Chat API

### Endpoint

```http
POST /api/chat
```

### Description

Accepts a user question and generates an AI response using the RAG pipeline and available MCP tools when appropriate.

The application retrieves relevant documentation chunks from Pinecone and supplies them as context to the language model. MCP tools can also provide historical incident information and service status.

### Request Headers

```http
Content-Type: application/json
```

### Request Body

```json
{
  "message": "How do I troubleshoot Payment API errors?"
}
```

| Field | Type | Description |
|---|---|---|
| message | String | User's question. Must not be blank. |

### Example cURL

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"How do I troubleshoot Payment API errors?"}'
```

### Successful Response

```json
{
  "answer": "Generated answer based on the available documentation and relevant tool results."
}
```

| Field | Type | Description |
|---|---|---|
| answer | String | AI-generated response. |

## 4. Snag Troubleshooting API

### Endpoint

```http
POST /api/snags/troubleshoot
```

### Description

Accepts a technical issue description and generates a structured troubleshooting response.

The service retrieves relevant documentation from Pinecone and can use MCP tools to search historical incidents or retrieve service status when appropriate.

### Request Headers

```http
Content-Type: application/json
```

### Request Body

```json
{
  "description": "Payment API is returning 500 errors"
}
```

| Field | Type | Description |
|---|---|---|
| description | String | Description of the technical issue. |

### Validation

The request description must:
- Not be blank.
- Contain at least 10 characters.
- Contain no more than 1000 characters.

### Example cURL

```bash
curl -X POST http://localhost:8080/api/snags/troubleshoot \
  -H "Content-Type: application/json" \
  -d '{"description":"Payment API is returning 500 errors"}'
```

### Successful Response

```json
{
  "problem": "Payment API is returning 500 errors",
  "possibleCauses": [
    "Database connection pool exhaustion",
    "A downstream dependency failure"
  ],
  "firstCheck": "Check the payment service logs and database connection pool metrics.",
  "nextSteps": [
    "Inspect recent application errors.",
    "Verify database connectivity.",
    "Review similar historical incidents."
  ]
}
```

The response above illustrates the response structure. Actual values are generated based on the available context and tool results.

| Field | Type | Description |
|---|---|---|
| problem | String | Summary of the reported issue. |
| possibleCauses | List of String | Potential causes of the issue. |
| firstCheck | String | First recommended diagnostic check. |
| nextSteps | List of String | Suggested troubleshooting actions. |

## 5. MCP Tools

The separate MCP Server runs on port `8090` and uses Streamable HTTP transport.

These tools are exposed through MCP. They are not public REST endpoints.

### 5.1 searchIncidents

**Purpose:** Search historical technical incidents using a keyword.

**Input:**

| Parameter | Type | Description |
|---|---|---|
| keyword | String | Keyword such as `payment`, `login`, or `order`. |

**Example input:**

```text
payment
```

**Example result:**

```text
INC001: Payment API returned 500 errors.
Cause: Database connection pool exhausted.
Resolution: Increased connection pool size.
```

The current implementation searches sample incident records stored in memory.

### 5.2 getServiceStatus

**Purpose:** Retrieve the status of a technical service.

**Input:**

| Parameter | Type | Description |
|---|---|---|
| serviceName | String | Name of the service to check. |

**Example input:**

```text
payment-service
```

**Example result:**

```text
UP
```

The current implementation uses predefined sample status values. It does not perform live health checks against actual services.

## 6. Validation and Error Handling

The REST request DTOs use Jakarta Bean Validation annotations to validate incoming requests.

- `ChatRequest`: message must not be blank.
- `SnagRequest`: description must not be blank and must satisfy the configured length constraints.

Invalid requests are handled through the application's configured validation and exception-handling mechanisms.

The exact error response structure should be documented after confirming the implementation of `GlobalExceptionHandler`.

## 7. Application Ports

| Application | Port | Purpose |
|---|---:|---|
| Main Spring Boot application | 8080 | REST APIs, RAG pipeline, MCP Client |
| Nexora MCP Server | 8090 | Exposes MCP tools |

## 8. Important Notes

- Document ingestion is handled by an internal ingestion pipeline. There is currently no public REST endpoint for document upload.
- MCP tools are accessed through the MCP Client connection, not by calling the tools as ordinary REST APIs.
- Incident records and service statuses currently use sample in-memory data.
- The examples in this document illustrate the API request and response structures. They should not be interpreted as verified runtime test results until the application has been tested.