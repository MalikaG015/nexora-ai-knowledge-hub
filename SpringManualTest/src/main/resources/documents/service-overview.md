
# Service Overview and FAQ

## System Overview

Our fictional company runs an online payment platform with three main services: Payment API, Order Service, and Notification Service.

The Payment API receives payment requests from clients. It validates each request and communicates with the Order Service to update the order status. After a payment is processed, the Notification Service sends confirmation messages.

The Payment API depends on a relational database to store payment records. It also communicates with an external payment provider to process transactions.

If a dependency becomes unavailable, the Payment API may return errors even when its own application code is functioning correctly.

## Service Health Checks

The Payment API exposes a health endpoint at `/health`. A successful response indicates that the application is responding to health checks, but it does not necessarily prove that every dependency is working correctly.

When investigating an incident, check application health, database connectivity, external provider availability, and recent deployment activity.

A health check returning HTTP 200 means the endpoint responded successfully. It should not be treated as proof that every payment operation will succeed.

## FAQ: Payment Requests

### Why is a payment request failing?

Check the HTTP status code, application logs, database connectivity, external payment provider status, and recent configuration changes. Do not assume the root cause until supporting evidence is available.

### Why is the Payment API slow?

Possible areas to investigate include database query latency, external provider response times, connection pool exhaustion, high request volume, and application resource usage.

### Why did payments stop after deployment?

Compare the new configuration with the previous working version. Check environment variables, database credentials, API endpoints, dependency versions, and startup logs.

### Does HTTP 200 mean the payment succeeded?

Not necessarily. HTTP 200 indicates that the HTTP request succeeded at the protocol level. Check the response body and payment status to determine whether the business operation succeeded.

### What should I collect before escalating an incident?

Collect the approximate start time, affected endpoint, HTTP status code, request or correlation ID, relevant error logs, recent deployments, and any observed dependency failures.

## Incident Investigation Principles

Start with observable evidence rather than assumptions. Correlate timestamps across services, compare healthy and failing requests, and distinguish confirmed findings from possible explanations.

Never invent log messages, metrics, service configurations, or root causes. If the available evidence is insufficient, state what is unknown and identify the next diagnostic check.
