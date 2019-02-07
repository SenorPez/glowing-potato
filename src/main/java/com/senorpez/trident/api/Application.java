package com.senorpez.trident.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.hateoas.hal.DefaultCurieProvider;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.http.MediaType.ALL;

@SpringBootApplication
class Application {
    private static final String HAL_OBJECT_MAPPER_BEAN_NAME = "_halObjectMapper";

    static final Set<SolarSystem> SOLAR_SYSTEMS = Collections.unmodifiableSet(getData(SolarSystem.class, "systems"));

    @Autowired
    private BeanFactory beanFactory;

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private static <T> Set<T> getData(final Class objectClass, final String field) {
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

    @Bean
    public HttpMessageConverters customConverters() {
        return new HttpMessageConverters(new Application.HalMappingJackson2HttpMessageConverter());
    }

    private class HalMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
        private HalMappingJackson2HttpMessageConverter() {
            setSupportedMediaTypes(Collections.singletonList(ALL));

            final ObjectMapper halObjectMapper = beanFactory.getBean(HAL_OBJECT_MAPPER_BEAN_NAME, ObjectMapper.class);
            setObjectMapper(halObjectMapper);
        }
    }

    @Bean
    public CurieProvider curieProvider() {
        return new DefaultCurieProvider("trident-api", new UriTemplate("/docs/{rel}"));
    }
}
