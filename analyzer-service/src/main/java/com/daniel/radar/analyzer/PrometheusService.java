package com.daniel.radar.analyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class PrometheusService {

    private static final Logger log = LoggerFactory.getLogger(PrometheusService.class);
    private final RestTemplate rest = new RestTemplate();

    public double queryP95(String service) {

        String q =
                "histogram_quantile(0.95, rate(http_latency_ms_milliseconds_bucket{service=\""
                        + service + "\"}[5m]))";   // <<< CORRECTO

        String encodedQuery = URLEncoder.encode(q, StandardCharsets.UTF_8);
        String finalUrl = "http://prometheus:9090/api/v1/query?query=" + encodedQuery;

        log.info("🔎 Ejecutando consulta a Prometheus: {}", finalUrl);

        try {
            Map response = rest.getForObject(new URI(finalUrl), Map.class);
            if (response == null || !response.containsKey("data")) return -1;

            Map data = (Map) response.get("data");
            List result = (List) data.get("result");
            if (result.isEmpty()) return -1;

            List value = (List) ((Map) result.get(0)).get("value");
            return Double.parseDouble((String) value.get(1));

        } catch (Exception e) {
            log.error("🔥 Error consultando Prometheus", e);
            return -1;
        }
    }
}
