package ru.brokilone.client.metrics;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ClientMetricsCatalog {

    public static final String NAMESPACE = "app_client_";

    public enum Type {
        COUNTER,
        GAUGE
    }

    public record Metric(String name, Type type, List<String> labelKeys) {
        public Metric {
            labelKeys = List.copyOf(labelKeys);
        }
    }

    public static final String CHECK_REQUESTS = NAMESPACE + "check_requests_total";
    public static final String CHECK_OUTCOME = "outcome";

    public static final List<Metric> METRICS = List.of(
            new Metric(CHECK_REQUESTS, Type.COUNTER, List.of(CHECK_OUTCOME))
    );

    private static final Map<String, Metric> BY_NAME = METRICS.stream()
            .collect(Collectors.toUnmodifiableMap(Metric::name, Function.identity()));

    public static Metric of(String name) {
        return BY_NAME.get(name);
    }

    private ClientMetricsCatalog() {
    }
}
