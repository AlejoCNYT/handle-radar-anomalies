package com.daniel.radar.analyzer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlertController {

    private final PrometheusService prom;
    private final ZScoreDetector detector;

    @Autowired
    public AlertController(PrometheusService prom, ZScoreDetector detector) {
        this.prom = prom;
        this.detector = detector;
    }

    @GetMapping("/alerts")
    public List<Alert> alerts() {
        double p95 = prom.queryP95("payment-api");
        return detector.evaluate("payment-api", p95)
                .map(List::of)
                .orElse(List.of());
    }
}
