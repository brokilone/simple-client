## Why

The client application currently emits no business/product metrics, so the organization cannot measure client-side product usage (feature adoption, conversion events, error rates in user flows). The `client-metrics` capability is already specified in the shared `spec-plans` store; this change implements it in the client application so its usage can be scraped by Prometheus alongside server metrics.

## What Changes

- Add a business-metrics capability to the client application: a registry of named counter/gauge business metrics, a recording API, and a `/metrics` Prometheus scrape endpoint.
- Expose metrics via Micrometer with the Prometheus registry (Spring Boot Actuator), consistent with the server.
- Instrument the client's business events — starting with the existing `/check` call path — using the recording API.
- Document the supported metric names, labels, and scrape configuration.

## Capabilities

### New Capabilities
- `client-metrics`: Business/product metrics for the client application — a registry of business metrics, recording APIs (counter/gauge), and a Prometheus scrape endpoint. Requirements are sourced from the authoritative `client-metrics` spec in the `spec-plans` store; no requirement changes.

### Modified Capabilities
<!-- None: no existing capabilities in this project's local spec tree. -->

## Impact

- Client application: new Micrometer/Prometheus metrics registry, a `/metrics` endpoint, and recording points at business-event call sites.
- New dependencies: `spring-boot-starter-actuator` and `micrometer-registry-prometheus`.
- Deployment: Prometheus scrape configuration updated to include the client's local `/metrics` endpoint (port 8082).
