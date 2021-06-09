package com.senorpez.trident.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.DefaultCurieProvider;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.DefaultLinkRelationProvider;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

class HALMessageConverter {
    static HttpMessageConverter<Object> getConverter(final List<MediaType> mediaType) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jackson2HalModule());

        final DefaultCurieProvider curieProvider = new DefaultCurieProvider("trident-api", UriTemplate.of("/docs/reference.html#resources-trident-{rel}"));
        final ResourcesRelProvider relProvider = new ResourcesRelProvider();
        final MessageResolver messageResolver = MessageResolver.DEFAULTS_ONLY;

        objectMapper.setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(relProvider, curieProvider, messageResolver));

        final MappingJackson2HttpMessageConverter halConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        halConverter.setObjectMapper(objectMapper);
        halConverter.setSupportedMediaTypes(mediaType);

        return halConverter;
    }

    private static class ResourcesRelProvider implements LinkRelationProvider {
        private static final DefaultLinkRelationProvider defaultRelProvider = new DefaultLinkRelationProvider();

        @Override
        public LinkRelation getItemResourceRelFor(Class<?> type) {
            final Relation[] relations = type.getAnnotationsByType(Relation.class);
            return relations.length > 0 ? LinkRelation.of(relations[0].itemRelation()) : defaultRelProvider.getItemResourceRelFor(type);
        }

        @Override
        public LinkRelation getCollectionResourceRelFor(Class<?> type) {
            final Relation[] relations = type.getAnnotationsByType(Relation.class);
            return relations.length > 0 ? LinkRelation.of(relations[0].collectionRelation()) : defaultRelProvider.getCollectionResourceRelFor(type);
        }

        @Override
        public boolean supports(LookupContext delimiter) {
            return defaultRelProvider.supports(delimiter);
        }
    }
}
