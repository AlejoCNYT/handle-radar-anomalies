package com.daniel.radar.analyzer;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class AlertStore {

    private final List<Alert> alerts = new ArrayList<>();

    public void add(Alert alert){
        alerts.add(alert);
    }

    public List<Alert> list(){
        return alerts;
    }
}
