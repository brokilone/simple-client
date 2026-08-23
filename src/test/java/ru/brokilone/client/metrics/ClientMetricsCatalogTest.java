package ru.brokilone.client.metrics;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientMetricsCatalogTest {

    @Test
    void counterNamesEndWithTotalSuffix() {
        ClientMetricsCatalog.METRICS.stream()
                .filter(metric -> metric.type() == ClientMetricsCatalog.Type.COUNTER)
                .forEach(metric -> assertTrue(metric.name().endsWith("_total"),
                        () -> metric.name() + " should end with _total"));
    }

    @Test
    void metricNamesUseClientNamespace() {
        ClientMetricsCatalog.METRICS.forEach(metric -> assertTrue(
                metric.name().startsWith(ClientMetricsCatalog.NAMESPACE),
                () -> metric.name() + " should start with " + ClientMetricsCatalog.NAMESPACE));
    }

    @Test
    void checkRequestsMetricHasOutcomeLabel() {
        ClientMetricsCatalog.Metric metric = ClientMetricsCatalog.of(ClientMetricsCatalog.CHECK_REQUESTS);

        assertEquals(ClientMetricsCatalog.Type.COUNTER, metric.type());
        assertEquals(List.of(ClientMetricsCatalog.CHECK_OUTCOME), metric.labelKeys());
    }
}
