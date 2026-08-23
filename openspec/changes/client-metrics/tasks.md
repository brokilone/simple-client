## 1. Dependencies & Configuration

- [ ] 1.1 Add `spring-boot-starter-actuator` and `micrometer-registry-prometheus` to `pom.xml` and verify the build compiles with `.\mvnw.cmd -q compile`
- [ ] 1.2 Configure Actuator in `application.properties` (`management.endpoints.web.base-path=/`, `management.endpoints.web.path-mapping.prometheus=metrics`, `management.endpoints.web.exposure.include=prometheus`) and verify `curl http://localhost:8082/metrics` returns HTTP 200 with Prometheus text format

## 2. Metrics Catalog & Recording API

- [ ] 2.1 Create `ClientMetricsCatalog` with compile-time constants (name + label keys + type) and verify counter names end with `_total` and use the `app_client_` prefix
- [ ] 2.2 Create `ClientMetrics` recording API (`increment` / `setGauge`) delegating to `MeterRegistry` and verify a unit test records and reads back a counter value

## 3. Instrumentation

- [ ] 3.1 Instrument `CheckClient.check()` to increment a check-requests counter with an `outcome` label (`success`/`not_found`/`bad_request`/`error`) and verify the counter appears in `/metrics` output with the `app_client_` prefix

## 4. Verification

- [ ] 4.1 Verify all business metrics use the `app_client_` namespace and are distinguishable from infrastructure metrics in `/metrics` output
- [ ] 4.2 Run `.\mvnw.cmd test` and verify the full test suite passes
