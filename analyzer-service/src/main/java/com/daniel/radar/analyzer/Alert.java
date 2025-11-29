package com.daniel.radar.analyzer;

import java.time.Instant;

public class Alert {

    public enum Severity { INFO, WARNING, CRITICAL }

    private String service;
    private String metric;
    private double value;
    private double threshold;
    private String detector;
    private Severity severity;
    private String timestamp;

    public Alert(String service, String metric, double value, double threshold, String detector, Severity severity) {
        this.service = service;
        this.metric = metric;
        this.value = value;
        this.threshold = threshold;
        this.detector = detector;
        this.severity = severity;
        this.timestamp = Instant.now().toString();
    }

    public Alert() {
        this.timestamp = Instant.now().toString();
        this.severity = Severity.INFO;
    }

    public String getService() { return service; }
    public String getMetric() { return metric; }
    public double getValue() { return value; }
    public double getThreshold() { return threshold; }
    public String getDetector() { return detector; }
    public Severity getSeverity() { return severity; }
    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "Alert{" +
                "service='" + service + '\'' +
                ", metric='" + metric + '\'' +
                ", value=" + value +
                ", threshold=" + threshold +
                ", detector='" + detector + '\'' +
                ", severity=" + severity +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
