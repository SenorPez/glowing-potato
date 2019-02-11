package com.senorpez.trident.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.core.DefaultRelProvider;
import org.springframework.hateoas.core.Relation;
import org.springframework.hateoas.hal.DefaultCurieProvider;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

class HALMessageConverter {
    static HttpMessageConverter<Object> getConverter(final List<MediaType> mediaType) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jackson2HalModule());

        final DefaultCurieProvider curieProvider = new DefaultCurieProvider("trident-api", new UriTemplate("/docs/reference.html#resources-trident-{rel}"));
        final ResourcesRelProvider relProvider = new ResourcesRelProvider();

        objectMapper.setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(relProvider, curieProvider, null));

        final MappingJackson2HttpMessageConverter halConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        halConverter.setObjectMapper(objectMapper);
        halConverter.setSupportedMediaTypes(mediaType);

        return halConverter;
    }

    private static class ResourcesRelProvider implements RelProvider {
        private static final DefaultRelProvider defaultRelProvider = new DefaultRelProvider();

        @Override
        public String getItemResourceRelFor(Class<?> type) {
            final Relation[] relations = type.getAnnotationsByType(Relation.class);
            return relations.length > 0 ? relations[0].value() : defaultRelProvider.getItemResourceRelFor(type);
        }

        @Override
        public String getCollectionResourceRelFor(Class<?> type) {
            final Relation[] relations = type.getAnnotationsByType(Relation.class);
            return relations.length > 0 ? relations[0].collectionRelation() : defaultRelProvider.getCollectionResourceRelFor(type);
        }

        @Override
        public boolean supports(Class<?> delimiter) {
            return defaultRelProvider.supports(delimiter);
        }
    }
}
