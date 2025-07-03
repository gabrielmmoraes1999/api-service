package io.github.gabrielmmoraes1999.service.config;

import io.github.gabrielmmoraes1999.apiservice.annotation.Bean;
import io.github.gabrielmmoraes1999.apiservice.annotation.Configuration;
import io.github.gabrielmmoraes1999.apiservice.ssl.SslProperties;

@Configuration
public class SslConfig {

    @Bean
    public SslProperties sslProperties() {
        return new SslProperties(
                true,
                "JKS",
                "/keystore.jks",
                "123456",
                "123456",
                443
        );
    }

}