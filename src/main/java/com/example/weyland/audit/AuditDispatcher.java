package com.example.weyland.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuditDispatcher {
    private final Logger log = LoggerFactory.getLogger(AuditDispatcher.class);
    private final AuditConfigProperties config;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public AuditDispatcher(AuditConfigProperties config, KafkaTemplate<String, String> kafkaTemplate) {
        this.config = config;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void dispatch(String auditMessage) {
        if ("console".equalsIgnoreCase(config.getMode())) {
            log.info("[AUDIT] {}", auditMessage);
        } else if ("kafka".equalsIgnoreCase(config.getMode())) {
            kafkaTemplate.send(config.getKafkaTopic(), auditMessage);
        } else {
            log.warn("Unknown audit mode: {}. Available modes: kafka, console", config.getMode());
        }
    }
}