package com.example.weyland.audit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "audit")
@Getter
@Setter
public class AuditConfigProperties {
    private String mode = "console";
    private String kafkaTopic = "android-audit";


}