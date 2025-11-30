package com.daniel.radar.ingestor;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class LatencyMetrics {

    private final Timer latencyP95;

    public LatencyMetrics(MeterRegistry registry) {
        latencyP95 = Timer.builder("http_latency_ms")
                .publishPercentileHistogram()
                .description("Latencias")
                .register(registry);
    }

    public void record(long valueMs) {
        latencyP95.record(valueMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
}
