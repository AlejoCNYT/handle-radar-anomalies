package com.daniel.radar.ingestor;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Random;

@RestController
public class MetricsController {

    // 🔥 NUEVO nombre estandarizado
    private final Timer latencyTimer;
    private final Random random = new Random();

    public MetricsController(MeterRegistry registry) {
        this.latencyTimer = Timer.builder("ingestor_latency_ms")  // <<— NOMBRE FINAL
                .publishPercentileHistogram()
                .publishPercentiles(0.5, 0.9, 0.95, 0.99)
                .tag("service", "payment-api")
                .description("Simulación de latencias del ingestor")
                .register(registry);
    }

    @GetMapping("/simulate")
    public String simulate() {
        latencyTimer.record(() -> {
            try { Thread.sleep(50 + random.nextInt(300)); }
            catch (InterruptedException ignored) {}
        });

        return "OK Latency simulated";
    }
}
