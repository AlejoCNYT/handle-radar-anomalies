package com.daniel.radar.ingestor;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class IngestController {

    private final DistributionSummary latencyHistogram;
    private final MeterRegistry registry;

    public IngestController(MeterRegistry registry) {
        this.registry = registry;

        // 🟢 Histograma persistente en memoria -> Prometheus puede leer *_bucket
        this.latencyHistogram = DistributionSummary.builder("http_latency_ms")
                .baseUnit("milliseconds")
                .description("Histogram of HTTP latencies in ms")
                .publishPercentileHistogram()        // <— genera buckets automáticos
                .publishPercentiles(0.5, 0.9, 0.95, 0.99)
                .register(registry);
    }

    @PostMapping
    public ResponseEntity<String> ingest(@RequestBody Event e) {

        // Graba latencia
        latencyHistogram.record(e.latency_ms);

        // Contador por error & servicio
        registry.counter("http_errors_total",
                        "service", e.service,
                        "status", String.valueOf(e.status_code))
                .increment();

        return ResponseEntity.ok("OK");
    }
}
