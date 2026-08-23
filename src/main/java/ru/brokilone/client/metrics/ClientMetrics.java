package ru.brokilone.client.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientMetrics {

    private final MeterRegistry meterRegistry;
    private final Map<String, Double> gaugeValues = new ConcurrentHashMap<>();

    public ClientMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void increment(String name, String... labelValues) {
        meterRegistry.counter(name, tags(name, labelValues)).increment();
    }

    public void setGauge(String name, double value, String... labelValues) {
        gaugeValues.computeIfAbsent(name, key -> {
            Gauge.builder(key, gaugeValues, values -> values.getOrDefault(key, 0.0))
                    .tags(tags(key, labelValues))
                    .register(meterRegistry);
            return 0.0;
        });
        gaugeValues.put(name, value);
    }

    private String[] tags(String name, String... labelValues) {
        ClientMetricsCatalog.Metric metric = ClientMetricsCatalog.of(name);
        String[] keys = metric == null ? new String[0] : metric.labelKeys().toArray(new String[0]);
        String[] tags = new String[keys.length * 2];
        for (int i = 0; i < keys.length; i++) {
            tags[i * 2] = keys[i];
            tags[i * 2 + 1] = i < labelValues.length ? labelValues[i] : "";
        }
        return tags;
    }
}
