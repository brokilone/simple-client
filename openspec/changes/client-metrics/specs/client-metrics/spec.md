## Purpose

Defines the business/product metrics capability for the client application, allowing the organization to measure client-side product usage and expose it for Prometheus scraping.

## ADDED Requirements

### Requirement: Client records named business metrics
The client application SHALL provide a mechanism to register and record named business metrics, where each metric has a stable name and a set of string labels.

#### Scenario: Record a counter metric
- **WHEN** client code records a business event for a registered counter metric with a given set of labels
- **THEN** the value of that metric for those labels increments by one

#### Scenario: Record a gauge metric
- **WHEN** client code sets a registered gauge metric to a numeric value for a given set of labels
- **THEN** the metric reflects that value when exposed

#### Scenario: Metric name and labels are stable
- **WHEN** a metric is registered with a name and label keys
- **THEN** that name and label key set SHALL remain fixed for the lifetime of the metric

### Requirement: Client exposes business metrics for scraping
The client application SHALL expose its recorded business metrics in the Prometheus text exposition format at a local scrape endpoint so a Prometheus server (or relay) can collect them.

#### Scenario: Scrape client metrics
- **WHEN** a Prometheus server sends an HTTP GET request to the client's local metrics endpoint
- **THEN** the client responds with HTTP 200 and a body containing every registered business metric in Prometheus text exposition format

### Requirement: Client business metrics follow the shared naming convention
Client business metrics SHALL follow the same naming convention as server metrics: a snake_case name that includes the application namespace and unit of measure, with a `_total` suffix for counters.

#### Scenario: Counter name has total suffix
- **WHEN** a business counter metric is registered on the client
- **THEN** its name SHALL end with `_total`

#### Scenario: Metric name includes client namespace
- **WHEN** a business metric is registered on the client
- **THEN** its name SHALL be prefixed with the client application namespace so client and server metrics for the same event are distinguishable
