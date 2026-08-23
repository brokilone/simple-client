package ru.brokilone.client.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientMetricsTest {

    @Test
    void incrementsCounterForGivenLabels() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        ClientMetrics metrics = new ClientMetrics(registry);

        metrics.increment(ClientMetricsCatalog.CHECK_REQUESTS, "success");
        metrics.increment(ClientMetricsCatalog.CHECK_REQUESTS, "success");
        metrics.increment(ClientMetricsCatalog.CHECK_REQUESTS, "error");

        Counter success = registry.get(ClientMetricsCatalog.CHECK_REQUESTS)
                .tag(ClientMetricsCatalog.CHECK_OUTCOME, "success")
                .counter();
        Counter error = registry.get(ClientMetricsCatalog.CHECK_REQUESTS)
                .tag(ClientMetricsCatalog.CHECK_OUTCOME, "error")
                .counter();

        assertEquals(2.0, success.count());
        assertEquals(1.0, error.count());
    }

    @Test
    void setsGaugeValue() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        ClientMetrics metrics = new ClientMetrics(registry);

        metrics.setGauge("app_client_test_gauge", 42.0);

        double value = registry.get("app_client_test_gauge").gauge().value();

        assertEquals(42.0, value);
    }
}
