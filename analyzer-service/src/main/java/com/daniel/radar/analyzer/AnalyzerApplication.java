package com.daniel.radar.analyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnalyzerApplication {

    private static final Logger log = LoggerFactory.getLogger(AnalyzerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AnalyzerApplication.class, args);
        log.info("✅ Analyzer service started and scheduling enabled.");
    }
}
