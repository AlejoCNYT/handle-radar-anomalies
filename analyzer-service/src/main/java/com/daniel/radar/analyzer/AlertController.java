package com.daniel.radar.analyzer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class AlertController {

    private final AlertStore store;

    public AlertController(AlertStore store){
        this.store = store;
    }

    @GetMapping("/alerts")
    public List<Alert> getAlerts(){
        return store.list();
    }
}
