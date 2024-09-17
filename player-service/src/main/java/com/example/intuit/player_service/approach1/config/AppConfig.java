package com.example.intuit.player_service.approach1.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;

@Configuration
@ConfigurationProperties(prefix = "app.approach1")
@Getter
@Setter
public class AppConfig {

    @NotBlank
    private String playerFilePath;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

