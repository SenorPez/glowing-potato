package com.senorpez.trident.api;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.util.Collections;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static com.senorpez.trident.api.DocumentationCommon.commonLinks;
import static com.senorpez.trident.api.SupportedMediaTypes.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RootControllerTest {
    private MockMvc mockMvc;
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "invalid+json", UTF_8);
    private static final ClassLoader CLASS_LOADER = RootControllerTest.class.getClassLoader();
    private static InputStream OBJECT_SCHEMA;
    private static InputStream ERROR_SCHEMA;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private final RestDocumentationResultHandler createLinksSnippets = document("links",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            relaxedLinks(halLinks(),
                    linkWithRel("self").description("This resource."),
                    linkWithRel("index").description("API index."),
                    linkWithRel("curies").description("Compact URI resolver.")));

    @Before
    public void setUp() {
        OBJECT_SCHEMA = CLASS_LOADER.getResourceAsStream("root.schema.json");
        ERROR_SCHEMA = CLASS_LOADER.getResourceAsStream("error.schema.json");

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new RootController())
                .setMessageConverters(HALMessageConverter.getConverter(Collections.singletonList(ALL)))
                .setControllerAdvice(new APIExceptionHandler())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void getRoot_ValidAcceptHeader() throws Exception {
        mockMvc.perform(get("/").accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.trident-api:systems", hasEntry("href", "http://localhost:8080/systems")))
                .andExpect(jsonPath("$._links.trident-api:constants", hasEntry("href", "http://localhost:8080/constants")))
                .andDo(createLinksSnippets)
                .andDo(document("index",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(TRIDENT_API_VALUE))),
                        commonLinks.and(
                                linkWithRel("trident-api:systems").description("List of solar system resources."),
                                linkWithRel("trident-api:constants").description("List of constant resources."))));
    }

    @Test
    public void getRoot_FallbackAcceptHeader() throws Exception {
        mockMvc.perform(get("/").accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchema(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.trident-api:systems", hasEntry("href", "http://localhost:8080/systems")))
                .andExpect(jsonPath("$._links.trident-api:constants", hasEntry("href", "http://localhost:8080/constants")));
    }

    @Test
    public void getRoot_InvalidAcceptHeader() throws Exception {
        mockMvc.perform(get("/").accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)));
    }

    @Test
    public void getRoot_InvalidMethod() throws Exception {
        mockMvc.perform(put("/").accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)));
    }
}
