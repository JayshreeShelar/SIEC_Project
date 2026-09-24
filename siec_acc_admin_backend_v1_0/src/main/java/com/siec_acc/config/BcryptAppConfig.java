package com.siec_acc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BcryptAppConfig {

    @Bean
    public BcryptEncoderConfig bCryptPasswordEncoder() {
        return new BcryptEncoderConfig();
    }
}