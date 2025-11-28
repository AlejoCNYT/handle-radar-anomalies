package com.daniel.radar.analyzer;

public class Alert {
    public String service;
    public String metric;
    public double value;
    public double threshold;
    public String detector;
    public String timestamp;

    public Alert(String service, String metric, double value, double threshold, String detector) {
        this.service = service;
        this.metric = metric;
        this.value = value;
        this.threshold = threshold;
        this.detector = detector;
        this.timestamp = java.time.Instant.now().toString();
    }
}
