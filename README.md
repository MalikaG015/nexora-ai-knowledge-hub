# Nexora AI Knowledge Hub

An AI-powered engineering knowledge assistant built using Java, Spring Boot, Spring AI, Retrieval-Augmented Generation (RAG), and Model Context Protocol (MCP).

## 1. Overview

Nexora AI Knowledge Hub helps developers find answers from engineering documentation and investigate technical issues using AI.

Instead of manually searching through multiple technical documents, developers can ask questions in natural language and receive answers based on relevant documentation.

The application combines two capabilities:

- **Retrieval-Augmented Generation (RAG):** Retrieves relevant information from engineering documentation stored in a vector database and provides it as context to a Large Language Model (LLM).
- **Model Context Protocol (MCP):** Allows the AI application to discover and invoke external tools, such as historical incident search and service-status lookup, through a standardized protocol.

By combining RAG and MCP, the application can retrieve documented technical knowledge and access additional tools when investigating engineering problems.

## 2. Key Features

### Documentation-based Question Answering
- Loads engineering documentation through an ingestion pipeline.
- Splits documents into smaller chunks.
- Generates embeddings using Ollama.
- Stores document embeddings in Pinecone.
- Retrieves relevant chunks using semantic similarity.
- Uses retrieved context to generate answers through an LLM.

### AI-powered Technical Troubleshooting
- Accepts technical problem descriptions.
- Retrieves relevant documentation.
- Generates structured troubleshooting responses.
- Provides possible causes, initial checks, and recommended next steps.

### MCP Tool Integration
The application connects to a separate MCP Server that exposes:

- `searchIncidents` – Searches sample historical incidents using keywords.
- `getServiceStatus` – Retrieves the status of a technical service.

The LLM can request these tools when they are relevant to the user's question.

### Reliability and Validation
- Validates incoming API requests.
- Handles failures from external dependencies.
- Provides structured troubleshooting responses.
- Separates the main AI application from the MCP Server.

## 3. Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot | Backend application framework |
| Spring AI | LLM integration and AI workflows |
| RAG | Retrieval of relevant engineering documentation |
| MCP | Standardized communication with external tools |
| Pinecone | Vector database |
| Ollama | Local embedding generation |
| Nomic Embed Text | Embedding model |
| Groq | LLM inference provider |
| Maven | Dependency management and build |

## 4. Application Architecture

The project contains two separate Spring Boot applications:

**1. Main Application – SpringManualTest**

Acts as the MCP Host and handles:
- REST API requests.
- Document retrieval.
- RAG workflow.
- LLM interaction through Spring AI.
- MCP Client integration.
- Technical troubleshooting.

**2. MCP Server – nexora-mcp-server**

Exposes tools that the main application can discover and invoke:
- Historical incident search.
- Service-status lookup.

The MCP Server runs independently from the main application.

Refer to [ARCHITECTURE.md](ARCHITECTURE.md) for the detailed architecture and request flows.

## 5. RAG Workflow

The document ingestion and retrieval workflow consists of the following steps.

### Document Ingestion

1. Engineering documents are loaded through the internal ingestion pipeline.
2. Documents are split into smaller chunks using text splitting.
3. Ollama generates embeddings for the chunks using the Nomic Embed Text model.
4. The chunks and their embeddings are stored in Pinecone.

### Question Answering

1. A developer submits a question.
2. The application generates a semantic search query.
3. Relevant document chunks are retrieved from Pinecone.
4. The retrieved chunks are added as context to the LLM request.
5. Groq generates a response grounded in the supplied documentation context.

## 6. MCP Workflow

1. The MCP Server exposes its registered tools.
2. The MCP Client in the main application connects to the MCP Server.
3. The client discovers available tools and their input schemas.
4. Spring AI makes the discovered tools available to the ChatClient.
5. The LLM determines whether a tool is required for the user's question.
6. Spring AI coordinates the tool invocation through the MCP Client.
7. The MCP Server executes the requested Java method.
8. The tool result is returned to the LLM through Spring AI.
9. The LLM generates the final response.

The MCP Server currently uses sample in-memory incident records and service-status values. These are not connected to a production incident database or live monitoring system.

Refer to [ARCHITECTURE.md](ARCHITECTURE.md) for the detailed MCP interaction flow.

## 7. REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/chat` | Answers engineering questions using the RAG workflow and available MCP tools. |
| POST | `/api/snags/troubleshoot` | Generates structured troubleshooting guidance for technical problems. |

Document ingestion is currently handled by an internal pipeline. There is no public REST endpoint for uploading documents.

Refer to [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for request formats, response examples, and validation details.

## 8. Configuration

The application requires the following configurations:

- Groq API key for LLM inference.
- Pinecone API key, index name, and namespace.
- Ollama base URL and embedding model.
- MCP Server connection URL.

Use environment variables for sensitive credentials instead of hardcoding API keys.

Example:

```properties
spring.ai.openai.api-key=${GROQ_API_KEY}
spring.ai.vectorstore.pinecone.api-key=${PINECONE_API_KEY}
```

The main application connects to the MCP Server at:

```text
http://localhost:8090
```

The MCP Server runs on port `8090`. The main application uses port `8080` in the current configuration.

## 9. Running the Applications

### Prerequisites

- Java 17
- Maven
- Ollama
- Pinecone account and configured vector index
- Groq API key

### Step 1: Start Ollama

Ensure Ollama is running and the embedding model is available:

```bash
ollama pull nomic-embed-text
```

### Step 2: Configure Environment Variables

Set the required credentials in your local environment:

```bash
export GROQ_API_KEY="your-groq-api-key"
export PINECONE_API_KEY="your-pinecone-api-key"
```

### Step 3: Start the MCP Server

Navigate to the MCP Server project:

```bash
cd nexora-mcp-server
```

Run:

```bash
./mvnw spring-boot:run
```

The MCP Server starts on port `8090`.

### Step 4: Start the Main Application

Open another terminal and navigate to the main application:

```bash
cd SpringManualTest
```

Run:

```bash
./mvnw spring-boot:run
```

The main application starts on port `8080`.

### Step 5: Call the REST APIs

Use Postman or curl to interact with the application.

See [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for examples.

## 10. Evaluation Approach

The application is intended to be evaluated using three criteria:

- **Correctness:** Does the answer address the user's question accurately?
- **Faithfulness:** Is the answer supported by the retrieved documentation or tool results?
- **Citation Quality:** Are the cited document titles and headings relevant to the answer?

Evaluation scenarios and actual results are maintained in [EVALUATION.md.

11. Current Scope and Limitations

Incident records are sample in-memory data.

Service-status values are hardcoded sample values, not live health checks.

Document ingestion is an internal pipeline rather than a public upload API.

Answers depend on the quality and availability of retrieved documentation and model output.

The application does not guarantee that every generated answer is correct.

End-to-end integration and evaluation results should be verified before claiming successful test completion.

12. Future Enhancements

Connect incident search to a real incident-management database.

Integrate live service health checks and monitoring systems.

Add document upload and management APIs.

Improve retrieval using metadata filtering and reranking.

Add authentication and authorization.

Expand automated evaluation and integration testing.

13. Repository Structure

Nexora-AI-Knowledge-Hub/
│
├── README.md
├── ARCHITECTURE.md
├── API_DOCUMENTATION.md
├── EVALUATION.md
│
├── SpringManualTest/
│   ├── pom.xml
│   └── src/
│
└── nexora-mcp-server/
├── pom.xml
└── src/

14. Project Goal

The goal of Nexora AI Knowledge Hub is to demonstrate how modern AI application development concepts can be integrated into a Java Spring Boot backend application.

It combines semantic document retrieval, LLM-powered response generation, structured troubleshooting, and MCP-based tool integration in a single project.