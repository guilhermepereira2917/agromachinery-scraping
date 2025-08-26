package br.com.oystr.agromachinery.scraping.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Spring configuration class for customizing and providing an {@link ObjectMapper} bean.
 */
@Configuration
public class ObjectMapperConfig {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
