package com.daniel.radar.analyzer;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertBroadcaster {

    private final SimpMessagingTemplate ws;
    private final AlertStore store;

    public AlertBroadcaster(SimpMessagingTemplate ws, AlertStore store){
        this.ws = ws;
        this.store = store;
    }

    public void send(Alert alert){
        store.add(alert);
        ws.convertAndSend("/topic/alerts", alert);
        System.out.println("📡 ALERTA GUARDADA/ENVIADA => " + alert);
    }
}
