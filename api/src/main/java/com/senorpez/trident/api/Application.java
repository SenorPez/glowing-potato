package com.senorpez.trident.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class Application {
    static final Set<SolarSystem> SOLAR_SYSTEMS = Collections.unmodifiableSet(getData(SolarSystem.class, "systems"));

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private static <T> Set<T> getData(final Class<?> objectClass, final String field) {
        final ObjectMapper mapper = new ObjectMapper();
        final ClassLoader classLoader = Application.class.getClassLoader();
        final InputStream inputStream = classLoader.getResourceAsStream("trident.json");
        try {
            final ObjectNode jsonData = mapper.readValue(inputStream, ObjectNode.class);
            return mapper.readValue(jsonData.get(field).toString(), mapper.getTypeFactory().constructCollectionType(Set.class, objectClass));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new HashSet<>();
    }

    static <T> Set<T> getData(final Class<?> objectClass, final JsonNode jsonNode) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonNode.toString(), mapper.getTypeFactory().constructCollectionType(Set.class, objectClass));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new HashSet<>();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }
}
