package io.github.gabrielmmoraes1999.service.config;

import io.github.gabrielmmoraes1999.apiservice.annotation.Bean;
import io.github.gabrielmmoraes1999.apiservice.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {

    @Bean
    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone("America/Sao_Paulo");
    }

}
