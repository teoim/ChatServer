package org.mtr.cloud.configservice.configuration;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("test-config")     // binds this class to application.properties entries prefixed by "test-config."
public class Configuration {
    private String config1;
    private String config2;
}
