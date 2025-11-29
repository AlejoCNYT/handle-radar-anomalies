package com.daniel.radar.analyzer;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ZScoreDetector {

    // Máximo de puntos históricos por servicio para no crecer en memoria sin límite
    private static final int MAX_HISTORY = 500;

    private final Map<String, List<Double>> history = new ConcurrentHashMap<>();

    /**
     * Evalúa si el valor actual (p95 de latencia) es anómalo usando Z-score.
     *
     * @param service nombre del servicio (ej: "payment-api")
     * @param value   valor actual del p95 (en ms) obtenido desde Prometheus
     * @return Optional con Alert si hay anomalía, o vacío si no.
     */
    public Optional<Alert> evaluate(String service, double value) {

        // ⚠️ Si no hay datos válidos de Prometheus todavía → NO generar alerta
        if (value < 0) {
            return Optional.empty();
        }

        history.putIfAbsent(service, new ArrayList<>());
        List<Double> list = history.get(service);

        // Añadimos el valor actual al historial
        list.add(value);
        if (list.size() > MAX_HISTORY) {
            // Ventana deslizante simple: nos quedamos con los últimos N
            list.remove(0);
        }

        // Necesitamos al menos 5 puntos para calcular estadística razonable
        if (list.size() < 5) {
            return Optional.empty();
        }

        double mean = list.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);

        double std = Math.sqrt(
                list.stream()
                        .mapToDouble(d -> Math.pow(d - mean, 2))
                        .sum() / list.size()
        );

        // Si no hay variación, no hay cómo calcular Z-score
        if (std == 0.0) {
            return Optional.empty();
        }

        double z = Math.abs((value - mean) / std);

        // Umbral típico de outlier: |z| >= 3
        if (z >= 3.0) {

            // Severidad según qué tan por encima está del promedio
            Alert.Severity severity =
                    value > mean * 1.5 ? Alert.Severity.CRITICAL : Alert.Severity.WARNING;

            return Optional.of(
                    new Alert(
                            service,
                            "latency_p95",   // nombre lógico de la métrica
                            value,
                            mean + 3 * std,  // threshold "esperado" p95
                            "zscore",
                            severity         // ✅ ahora coincide con el constructor
                    )
            );
        }

        return Optional.empty();
    }
}
