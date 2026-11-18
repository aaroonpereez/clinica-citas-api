package com.clinica.citas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {

    /** Reloj del sistema. En los tests se sustituye por un Clock.fixed(...) para controlar "ahora". */
    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
