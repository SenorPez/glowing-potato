package com.senorpez.trident.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.config.HypermediaMappingInformation;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.DefaultCurieProvider;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.DefaultLinkRelationProvider;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.*;

@Configuration
class SupportedMediaTypes implements HypermediaMappingInformation {
    static final MediaType TRIDENT_API = new MediaType("application", "vnd.senorpez.trident.v1+json", UTF_8);
    static final String TRIDENT_API_VALUE = "application/vnd.senorpez.trident.v1+json; charset=UTF-8";

    static final MediaType FALLBACK = APPLICATION_JSON;
    static final String FALLBACK_VALUE = APPLICATION_JSON_VALUE;

    @Override
    @NonNull
    public List<MediaType> getMediaTypes() {
        return MediaType.parseMediaTypes(Collections.singletonList(ALL_VALUE));
    }

    @Override
    @NonNull
    public ObjectMapper configureObjectMapper(ObjectMapper mapper) {
        mapper.registerModule(new Jackson2HalModule());

        final LinkRelationProvider relProvider = new RelProvider();
        final CurieProvider curieProvider = curieProvider();
        final MessageResolver messageResolver = MessageResolver.DEFAULTS_ONLY;

        mapper.setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(relProvider, curieProvider, messageResolver));

        return mapper;
    }

    @Bean
    public CurieProvider curieProvider() {
        return new DefaultCurieProvider("trident-api", UriTemplate.of("/docs/reference.html#resources-trident-{rel}"));
    }

    public HttpMessageConverter<Object> getConverter(final List<MediaType> mediaTypes) {
        final ObjectMapper objectMapper = configureObjectMapper(new ObjectMapper());
        final MappingJackson2HttpMessageConverter halConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        halConverter.setObjectMapper(objectMapper);
        halConverter.setSupportedMediaTypes(mediaTypes);

        return halConverter;
    }

    private static class RelProvider implements LinkRelationProvider {
        private static final DefaultLinkRelationProvider defaultRelProvider = new DefaultLinkRelationProvider();

        @Override
        @NonNull
        public LinkRelation getItemResourceRelFor(Class<?> type) {
            final Relation[] relations = type.getAnnotationsByType(Relation.class);
            return relations.length > 0 ? LinkRelation.of(relations[0].itemRelation()) : defaultRelProvider.getItemResourceRelFor(type);
        }

        @Override
        @NonNull
        public LinkRelation getCollectionResourceRelFor(Class<?> type) {
            final Relation[] relations = type.getAnnotationsByType(Relation.class);
            return relations.length > 0 ? LinkRelation.of(relations[0].collectionRelation()) : defaultRelProvider.getCollectionResourceRelFor(type);
        }

        @Override
        @NonNull
        public boolean supports(@NonNull LookupContext delimiter) {
            return defaultRelProvider.supports(delimiter);
        }
    }

}
