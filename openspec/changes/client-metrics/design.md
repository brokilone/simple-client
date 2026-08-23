## Context

The client is a Spring Boot webmvc application (Java 17, Spring Boot 4.1.1) running on port 8082. It currently consists of a single `CommandLineRunner` (`CheckClient`) that calls the server's `/check` endpoint and logs the outcome. There are no business/product metrics today. See proposal.md for motivation and specs/client-metrics/spec.md for the requirements.

## Goals / Non-Goals

**Goals:**
- Expose business metrics on a Prometheus scrape endpoint with minimal configuration.
- A minimal-touch recording API so developers can instrument business events without boilerplate.
- Enforce the shared naming convention (`app_client_` namespace, `_total` counter suffix) at compile time.

**Non-Goals:**
- Infrastructure/technical metrics (HTTP/JVM/GC) — provided by the metrics library, not defined here.
- Metrics storage, dashboards, or alerting rules.
- Distributed tracing or log correlation.

## Decisions

### Decision 1: Micrometer with the Prometheus registry via Spring Boot Actuator
- Add `spring-boot-starter-actuator` and `micrometer-registry-prometheus`; Micrometer `MeterRegistry` is the de-facto Spring standard and integrates with the web stack with no custom code.
- **Alternatives considered**: Prometheus Java simpleclient with an embedded HTTP server — heavier and unnecessary now that the client is a webmvc app (the archived design assumed a non-web client); a hand-rolled registry — error-prone and non-standard.

### Decision 2: Expose the Prometheus endpoint at `/metrics`
- Configure Actuator (`management.endpoints.web.base-path=/`, `management.endpoints.web.path-mapping.prometheus=metrics`) so the scrape endpoint is `/metrics`, matching the server for parity.
- **Alternative considered**: the Actuator default `/actuator/prometheus` — rejected for inconsistency with the server's `/metrics`.

### Decision 3: Business metrics are enumerated in a compile-time catalog
- Each business metric is declared as a constant (name + label keys + type) in a single `ClientMetricsCatalog`, rather than ad-hoc strings at call sites.
- **Rationale**: enforces the naming convention and label stability required by the spec, and gives one place to audit all client business metrics.
- **Alternative considered**: free-form string registration — rejected because it allows naming drift and dynamic label keys.

### Decision 4: Namespace and unit conventions
- Client business metrics use the `app_client_` prefix; counters use `_total`; current-state values use gauges.
- **Rationale**: a single Prometheus can scrape both client and server and distinguish the same event by namespace (mirrors the shared convention in the `spec-plans` store).

### Decision 5: Thin recording API over `MeterRegistry`
- A `ClientMetrics` component exposes `increment(String name, String... labelValues)` and `setGauge(String name, double value, String... labelValues)`, delegating to `MeterRegistry` counters/gauges registered from the catalog.
- **Rationale**: isolates call sites from Micrometer details and keeps label-key ordering consistent with the catalog.

## Risks / Trade-offs

- **Label cardinality explosion** → Restrict labels to a fixed, documented set per metric (no user IDs or unbounded values); the catalog enforces the key set.
- **Actuator footprint** → Pulling in Actuator adds endpoints and a dependency surface; acceptable because it is the standard Spring Boot path and its exposure is limited via configuration.
- **Metrics endpoint reachability** → The client's local `/metrics` may be unreachable from a central Prometheus in some deployments; document a relay/pushgateway option as a future follow-up (non-goal now).
- **Naming convention drift** → Mitigated by the compile-time catalog and code review.
- **Endpoint exposure** → `/metrics` is unauthenticated; keep it local-only and do not expose beyond the host in production.

## Migration Plan

1. Add the two dependencies and the Actuator configuration; verify the build compiles and `/metrics` returns HTTP 200 with Prometheus text format.
2. Add the `ClientMetricsCatalog` and `ClientMetrics` recording API.
3. Instrument the `/check` call path (a check-requests counter with an `outcome` label).
4. Verify recorded metrics appear in `/metrics` output with the `app_client_` prefix.
5. Update the Prometheus scrape config to include the client endpoint.
6. Rollback: removing the dependencies and Actuator configuration restores prior behavior; the change is additive and non-breaking.
