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

        this.latencyHistogram = DistributionSummary.builder("http_latency_ms")
                .baseUnit("milliseconds")
                .description("Histogram of HTTP latencies in ms")

                // 🔥 CREACIÓN DE BUCKET VISIBLES PARA PROMETHEUS
                .serviceLevelObjectives(100,300,500,1000,2000,3000,5000)

                .publishPercentileHistogram()
                .publishPercentiles(0.5,0.9,0.95,0.99)

                .tag("service","payment-api") // coincidimos con alertas y consultas
                .register(registry);
    }

    @PostMapping
    public ResponseEntity<String> ingest(@RequestBody Event e) {

        latencyHistogram.record(e.latency_ms);

        registry.counter("http_errors_total",
                        "service",e.service,
                        "status",String.valueOf(e.status_code))
                .increment();

        return ResponseEntity.ok("OK");
    }
}
