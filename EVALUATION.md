# Evaluation Report

## 1. Overview

This document defines the evaluation approach for Nexora AI Knowledge Hub.

The goal is to assess whether the assistant provides correct, context-grounded troubleshooting answers using retrieved documentation and MCP tool results.

The evaluation focuses on three criteria:

1. Correctness
2. Faithfulness
3. Citation Quality

**Evaluation status:** Pending execution.

No evaluation scores or pass results are claimed in this document.

## 2. Evaluation Criteria

### 2.1 Correctness

Checks whether the generated answer is technically correct and addresses the user's question.

**Pass when:**
- The answer correctly identifies the issue or answers the question.
- Suggested causes and troubleshooting steps are technically reasonable.
- Tool results are interpreted correctly.

**Fail when:**
- The answer contains incorrect technical information.
- The answer misunderstands the reported issue.
- The answer recommends an incorrect or misleading action.

### 2.2 Faithfulness

Checks whether the answer is supported by the information retrieved from documentation or returned by MCP tools.

**Pass when:**
- Documentation-based claims are supported by retrieved documentation.
- Historical incident details match the MCP tool results.
- Service status matches the result returned by the MCP tool.
- The assistant does not invent unsupported technical details.

**Fail when:**
- The answer introduces information that is not supported by the available context.
- The assistant invents incident details or service status.
- The answer makes claims that contradict retrieved information.

### 2.3 Citation Quality

Checks whether documentation-based answers identify the correct source.

**Pass when:**
- The cited page title is correct.
- The cited heading is relevant to the answer.
- The citation supports the statement being made.

**Fail when:**
- A citation is missing where documentation is used.
- The page title or heading is incorrect.
- The citation does not support the associated claim.

MCP tool results should be clearly identified as tool-derived information rather than presented as documentation citations.

## 3. Evaluation Scenarios

The following scenarios will be used to evaluate the application.

| Test ID | Scenario | Expected Behavior |
|---|---|---|
| EVAL-01 | Ask a question whose answer exists in the documentation. | Retrieve relevant chunks and provide a grounded answer with source references. |
| EVAL-02 | Ask a question whose answer is absent from the documentation. | Avoid inventing an answer and use the configured fallback response. |
| EVAL-03 | Ask a question requiring information from multiple documentation chunks. | Combine relevant information without introducing unsupported claims. |
| EVAL-04 | Ask about a similar historical incident. | Invoke `searchIncidents` when appropriate and report matching incident details. |
| EVAL-05 | Ask for the status of a known service. | Invoke `getServiceStatus` when appropriate and report the returned status. |
| EVAL-06 | Ask a troubleshooting question requiring documentation and incident history. | Combine relevant RAG context and MCP results, distinguishing their sources. |
| EVAL-07 | Ask a question that cannot be answered from available documentation or tool results. | Clearly communicate the information limitation instead of fabricating details. |

## 4. Evaluation Procedure

For each scenario:

1. Submit the test question to the relevant API.
2. Record the generated response.
3. Record the retrieved documentation context, where applicable.
4. Record MCP tool invocation and results, where applicable.
5. Compare the answer against the available source information.
6. Mark Correctness, Faithfulness, and Citation Quality as Pass or Fail.
7. Record unsupported statements, incorrect citations, or missing information.

## 5. Evaluation Results

Evaluation results will be recorded after executing the test scenarios.

**Current status: Not executed.**

| Test ID | Correctness | Faithfulness | Citation Quality | Notes |
|---|---|---|---|---|
| EVAL-01 | Pending | Pending | Pending | Awaiting execution |
| EVAL-02 | Pending | Pending | Pending | Awaiting execution |
| EVAL-03 | Pending | Pending | Pending | Awaiting execution |
| EVAL-04 | Pending | Pending | Pending | Awaiting execution |
| EVAL-05 | Pending | Pending | Pending | Awaiting execution |
| EVAL-06 | Pending | Pending | Pending | Awaiting execution |
| EVAL-07 | Pending | Pending | Pending | Awaiting execution |

## 6. Detailed Evaluation Record

Use the following template for each executed scenario.

### Test ID: EVAL-XX

**Input Question:**

Record the question submitted to the assistant.

**Expected Behavior:**

Describe what the assistant should retrieve, invoke, or answer.

**Actual Response:**

Paste the generated response.

**Retrieved Documentation:**

Record the relevant page titles, headings, and retrieved content, if applicable.

**MCP Tool Results:**

Record the tool invoked and its returned result, if applicable.

**Evaluation:**

| Criterion | Result | Observation |
|---|---|---|
| Correctness | Pending | To be evaluated |
| Faithfulness | Pending | To be evaluated |
| Citation Quality | Pending | To be evaluated |

**Unsupported Sentence:**

Record any statement that is not supported by the available sources, or write `None`.

**Incorrect or Missing Citation:**

Record any citation issue, or write `None`.

**Additional Notes:**

Record observations and possible improvements.

## 7. Known Evaluation Limitations

- Current incident records are sample in-memory data.
- Current service statuses are predefined values, not live service monitoring results.
- Evaluation scores have not yet been measured.
- End-to-end MCP behavior must be verified through application testing.
- The evaluation scenarios are a proposed test plan, not evidence of completed test execution.

## 8. Future Improvements

- Build a larger set of realistic engineering questions.
- Evaluate retrieval relevance and whether the correct chunks are retrieved.
- Measure answer quality across repeated runs.
- Add automated evaluation where practical.
- Expand incident data and connect service-status tools to real monitoring systems.
- Track evaluation results across application changes.

## 9. Conclusion

Nexora AI Knowledge Hub uses a three-part evaluation approach covering technical correctness, faithfulness to retrieved information, and citation quality.

This approach is intended to identify unsupported answers, incorrect technical recommendations, and weak source attribution before the application is considered reliable for real-world use.