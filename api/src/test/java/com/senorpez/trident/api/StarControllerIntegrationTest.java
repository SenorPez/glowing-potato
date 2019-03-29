package com.senorpez.trident.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static com.senorpez.trident.api.SupportedMediaTypes.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.EMPTY_SET;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(Parameterized.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StarControllerIntegrationTest {
    private MockMvc mockMvc;
    private TestContextManager testContextManager;
    
    @Autowired
    private WebApplicationContext wac;
    
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "vnd.senorpez.trident.v0+json", UTF_8);
    private static final ClassLoader CLASS_LOADER = StarControllerTest.class.getClassLoader();
    private static InputStream STAR_SCHEMA;
    private static InputStream STAR_COLLECTION_SCHEMA;
    private static InputStream ERROR_SCHEMA;
    
    private final int systemId;
    private final int starId;

    @Parameterized.Parameters(name = "systemId: {0}, starId: {1}")
    public static Collection params() {
        final ObjectMapper mapper = new ObjectMapper();
        final ClassLoader classLoader = Application.class.getClassLoader();
        final InputStream inputStream = classLoader.getResourceAsStream("trident.json");
        try {
            final ObjectNode jsonData = mapper.readValue(inputStream, ObjectNode.class);
            Set<SolarSystem> systems = mapper.readValue(jsonData.get("systems").toString(), mapper.getTypeFactory().constructCollectionType(Set.class, SolarSystem.class));
            return systems.stream()
                    .flatMap(solarSystem -> solarSystem.getStars().stream()
                    .map(star -> new Object[]{solarSystem.getId(), star.getId()}))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EMPTY_SET;
    }
    
    public StarControllerIntegrationTest(int systemId, int starId) {
        this.systemId = systemId;
        this.starId = starId;
    }
    
    @Before
    public void setUp() throws Exception {
        STAR_SCHEMA = CLASS_LOADER.getResourceAsStream("star.schema.json");
        STAR_COLLECTION_SCHEMA = CLASS_LOADER.getResourceAsStream("stars.schema.json");
        ERROR_SCHEMA = CLASS_LOADER.getResourceAsStream("error.schema.json");

        testContextManager = new TestContextManager(getClass());
        testContextManager.prepareTestInstance(this);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    public void GetAllStars_ValidSystemId_ValidAcceptHeader() throws Exception {
        mockMvc.perform(get(String.format("/systems/%d/stars/", systemId)).accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(STAR_COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.trident-api:star", hasItem(
                        allOf(
                                hasEntry("id", (Object) starId),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format(
                                                        "http://localhost/systems/%d/stars/%d",
                                                        systemId,
                                                        starId))))))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost/systems/%d/stars", systemId))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:system", hasEntry("href", String.format(
                        "http://localhost/systems/%d", systemId))));
    }

    @Test
    public void GetAllStars_ValidSystemId_FallbackAcceptHeader() throws Exception {
        mockMvc.perform(get(String.format("/systems/%d/stars/", systemId)).accept(FALLBACK_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK_VALUE))
                .andExpect(content().string(matchesJsonSchema(STAR_COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.trident-api:star", hasItem(
                        allOf(
                                hasEntry("id", (Object) starId),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format(
                                                        "http://localhost/systems/%d/stars/%d",
                                                        systemId,
                                                        starId))))))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost/systems/%d/stars", systemId))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:system", hasEntry("href", String.format(
                        "http://localhost/systems/%d", systemId))));
    }

        @Test
    public void GetAllStars_ValidSystemId_InvalidAcceptHeader() throws Exception {

        mockMvc.perform(get(String.format("/systems/%d/stars/", systemId)).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));
    }

    @Test
    public void GetAllStars_ValidSystemId_InvalidMethod() throws Exception {
        mockMvc.perform(put(String.format("/systems/%d/stars/", systemId)).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));
    }

    @Test
    public void GetAllStars_InvalidSystemId_ValidAcceptHeader() throws Exception {
        mockMvc.perform(get("/systems/8675309/stars/").accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Solar system with ID of %d not found", 8675309))));
    }

    @Test
    public void GetAllStars_InvalidSystemId_FallbackAcceptHeader() throws Exception {
        mockMvc.perform(get("/systems/8675309/stars/").accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Solar system with ID of %d not found", 8675309))));
    }

    @Test
    public void GetAllStars_InvalidSystemId_InvalidAcceptHeader() throws Exception {
        mockMvc.perform(get("/systems/8675309/stars/").accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));
    }

    @Test
    public void GetAllStars_InvalidSystemId_InvalidMethod() throws Exception {
        mockMvc.perform(put("/systems/8675309/stars").accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));
    }
    
    @Test
    public void GetSingleStar_ValidSystemId_ValidStarId_ValidAcceptHeader() throws Exception {
        mockMvc.perform(get(String.format("/systems/%d/stars/%d", systemId, starId)).accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(STAR_SCHEMA)))
                .andExpect(jsonPath("$.id", is(starId)))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost/systems/%d/stars/%d",
                        systemId,
                        starId))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:stars", hasEntry("href", String.format(
                        "http://localhost/systems/%d/stars", systemId))));
    }
    
    @Test
    public void GetSingleStar_ValidSystemId_ValidStarId_FallbackAcceptHeader() throws Exception {
        mockMvc.perform(get(String.format("/systems/%d/stars/%d", systemId, starId)).accept(FALLBACK_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK_VALUE))
                .andExpect(content().string(matchesJsonSchema(STAR_SCHEMA)))
                .andExpect(jsonPath("$.id", is(starId)))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost/systems/%d/stars/%d",
                        systemId,
                        starId))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:stars", hasEntry("href", String.format(
                        "http://localhost/systems/%d/stars", systemId))));
    }
    
    @Test
    public void GetSingleStar_ValidSystemId_ValidStarId_InvalidAcceptHeader() throws Exception {
        mockMvc.perform(get(String.format("/systems/%d/stars/%d", systemId, starId)).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));
    }

    @Test
    public void GetSingleStar_ValidSystemId_ValidStarId_InvalidMethod() throws Exception {
        mockMvc.perform(put(String.format("/systems/%d/stars/%d", systemId, starId)).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));
    }

    @Test
    public void GetSingleStar_ValidSystemId_InvalidStarId_ValidAcceptHeader() throws Exception {
        mockMvc.perform(get(String.format("/systems/%d/stars/8675309", systemId)).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Star with ID of %d not found", 8675309))));
    }

    @Test
    public void GetSingleStar_ValidSystemId_InvalidStarId_FallbackAcceptHeader() throws Exception {
        mockMvc.perform(get(String.format("/systems/%d/stars/8675309", systemId)).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Star with ID of %d not found", 8675309))));
    }

    @Test
    public void GetSingleStar_ValidSystemId_InvalidStarId_InvalidAcceptHeader() throws Exception {
        mockMvc.perform(get(String.format("/systems/%d/stars/8675309", systemId)).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));
    }

    @Test
    public void GetSingleStar_ValidSystemId_InvalidStarId_InvalidMethod() throws Exception {
        mockMvc.perform(put(String.format("/systems/%d/stars/8675309", systemId)).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));
    }

    @Test
    public void GetSingleStar_InvalidSystemId_XXXStarId_ValidAcceptHeader() throws Exception {
        mockMvc.perform(get(String.format("/systems/8675309/stars/%d", systemId)).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Solar system with ID of %d not found", 8675309))));
    }

    @Test
    public void GetSingleStar_InvalidSystemId_XXXStarId_FallbackAcceptHeader() throws Exception {
        mockMvc.perform(get(String.format("/systems/8675309/stars/%d", systemId)).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Solar system with ID of %d not found", 8675309))));
    }

    @Test
    public void GetSingleStar_InvalidSystemId_XXXStarId_InvalidAcceptHeader() throws Exception {
        mockMvc.perform(get(String.format("/systems/8675309/stars/%d", systemId)).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));
    }

    @Test
    public void GetSingleStar_InvalidSystemId_XXXStarId_InvalidMethod() throws Exception {
        mockMvc.perform(put(String.format("/systems/8675309/stars/%d", systemId)).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));
    }
}