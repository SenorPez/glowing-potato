package com.senorpez.trident.api;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static com.senorpez.trident.api.DocumentationCommon.commonLinks;
import static com.senorpez.trident.api.SupportedMediaTypes.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PlanetControllerTest {
    private MockMvc mockMvc;
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "vnd.senorpez.trident.v0+json", UTF_8);
    private static final ClassLoader CLASS_LOADER = PlanetControllerTest.class.getClassLoader();
    private static InputStream PLANET_SCHEMA;
    private static InputStream PLANET_COLLECTION_SCHEMA;
    private static InputStream ERROR_SCHEMA;

    private static final Planet FIRST_PLANET = new PlanetBuilder()
            .setId(111)
            .setName("1 Eta Veneris 1")
            .setMass((float) 0.400395313)
            .setRadius((float) 0.79729792)
            .setSemimajorAxis((float) 0.092467461)
            .setEccentricity((float) 0.12)
            .setInclination((float) 0.094723905)
            .setLongitudeOfAscendingNode((float) 2.698937013)
            .setArgumentOfPeriapsis((float) 0.82315138)
            .setTrueAnomalyAtEpoch((float) 1.494408501)
            .build();

    private static final Planet SECOND_PLANET = new PlanetBuilder()
            .setId(112)
            .setName("1 Eta Veneris 2")
            .setMass((float) 0.033057826)
            .setRadius((float) 0.359753736)
            .setSemimajorAxis((float) 0.314389366)
            .setEccentricity((float) 0.005)
            .setInclination((float) 0.01818743)
            .setLongitudeOfAscendingNode((float) 2.026952089)
            .setArgumentOfPeriapsis((float) 4.952656241)
            .setTrueAnomalyAtEpoch((float) 1.517031459)
            .build();

    private static final Planet THIRD_PLANET = new PlanetBuilder()
            .setId(211)
            .setName("Earth")
            .setMass((float) 1)
            .setRadius((float) 1)
            .setSemimajorAxis((float) 1)
            .setEccentricity((float) 0.01671022)
            .setInclination((float) 0.00005)
            .setLongitudeOfAscendingNode((float) 0)
            .setArgumentOfPeriapsis((float) 0)
            .setTrueAnomalyAtEpoch((float) 0)
            .build();

    private static final Star FIRST_STAR = new StarBuilder()
            .setId(11)
            .setName("1 Eta Veneris")
            .setMass((float) 0.75)
            .setPlanets(new HashSet<>(Arrays.asList(
                    FIRST_PLANET,
                    SECOND_PLANET)))
            .build();

    private static final Star SECOND_STAR = new StarBuilder()
            .setId(21)
            .setName("Sol")
            .setMass((float) 1)
            .setPlanets(new HashSet<>(Collections.singletonList(
                    THIRD_PLANET)))
            .build();

    private static final SolarSystem FIRST_SYSTEM = new SolarSystemBuilder()
            .setId(1)
            .setName("Eta Veneris")
            .setStars(new HashSet<>(Arrays.asList(
                    FIRST_STAR,
                    SECOND_STAR)))
            .build();

    private static final Star EMPTY_STAR = new StarBuilder()
            .setId(31)
            .setName("Empty")
            .setMass((float) 1)
            .setPlanets(null)
            .build();

    @InjectMocks
    PlanetController planetController;

    @Mock
    private APIService apiService;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Before
    public void SetUp() {
        PLANET_SCHEMA = CLASS_LOADER.getResourceAsStream("planet.schema.json");
        PLANET_COLLECTION_SCHEMA = CLASS_LOADER.getResourceAsStream("planets.schema.json");
        ERROR_SCHEMA = CLASS_LOADER.getResourceAsStream("error.schema.json");
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new PlanetController(apiService))
                .setMessageConverters(HALMessageConverter.getConverter(Collections.singletonList(ALL)))
                .setControllerAdvice(new APIExceptionHandler())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void GetAllPlanets_ValidSystemId_ValidStarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), FIRST_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(PLANET_COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.trident-api:planet", hasItem(
                        allOf(
                                hasEntry("id", (Object) FIRST_PLANET.getId()),
                                hasEntry("name", (Object) FIRST_PLANET.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format(
                                                        "http://localhost:8080/systems/%d/stars/%d/planets/%d",
                                                        FIRST_SYSTEM.getId(),
                                                        FIRST_STAR.getId(),
                                                        FIRST_PLANET.getId()))))))))
                .andExpect(jsonPath("$._embedded.trident-api:planet", hasItem(
                        allOf(
                                hasEntry("id", (Object) SECOND_PLANET.getId()),
                                hasEntry("name", (Object) SECOND_PLANET.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format(
                                                        "http://localhost:8080/systems/%d/stars/%d/planets/%d",
                                                        FIRST_SYSTEM.getId(),
                                                        FIRST_STAR.getId(),
                                                        SECOND_PLANET.getId()))))))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), FIRST_STAR.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:star", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId()))))
                .andDo(document("planets",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(TRIDENT_API_VALUE))),
                        responseFields(
                                fieldWithPath("_embedded.trident-api:planet").description("Planet resource."),
                                fieldWithPath("_embedded.trident-api:planet[].id").description("Planet ID number."),
                                fieldWithPath("_embedded.trident-api:planet[].name").description("Planet name."),
                                subsectionWithPath("_links").ignored(),
                                subsectionWithPath("_embedded.trident-api:planet[]._links").ignored()),
                        commonLinks.and(
                                linkWithRel("trident-api:star").description("Star resource."))));

        verify(apiService, times(2)).findOne(any(), any(), any());
    }

    @Test
    public void GetAllPlanets_ValidSystemId_ValidStarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), FIRST_STAR.getId())).accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchema(PLANET_COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.trident-api:planet", hasItem(
                        allOf(
                                hasEntry("id", (Object) FIRST_PLANET.getId()),
                                hasEntry("name", (Object) FIRST_PLANET.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format(
                                                        "http://localhost:8080/systems/%d/stars/%d/planets/%d",
                                                        FIRST_SYSTEM.getId(),
                                                        FIRST_STAR.getId(),
                                                        FIRST_PLANET.getId()))))))))
                .andExpect(jsonPath("$._embedded.trident-api:planet", hasItem(
                        allOf(
                                hasEntry("id", (Object) SECOND_PLANET.getId()),
                                hasEntry("name", (Object) SECOND_PLANET.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format(
                                                        "http://localhost:8080/systems/%d/stars/%d/planets/%d",
                                                        FIRST_SYSTEM.getId(),
                                                        FIRST_STAR.getId(),
                                                        SECOND_PLANET.getId()))))))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), FIRST_STAR.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:star", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId()))));

        verify(apiService, times(2)).findOne(any(), any(), any());
    }

    @Test
    public void GetAllPlanets_ValidSystemId_ValidStarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), FIRST_STAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_ValidSystemId_ValidStarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR);

        mockMvc.perform(put(String.format("/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), FIRST_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_ValidSystemId_InvalidStarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/%d/stars/8675309/planets", FIRST_SYSTEM.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Star with ID of %d not found", 8675309))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_ValidSystemId_InvalidStarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/%d/stars/8675309/planets", FIRST_SYSTEM.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Star with ID of %d not found", 8675309))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_ValidSystemId_InvalidStarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/%d/stars/8675309/planets", FIRST_SYSTEM.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_ValidSystemId_InvalidStarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(put(String.format("/systems/%d/stars/8675309/planets", FIRST_SYSTEM.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_InvalidSystemId_XXXStarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/8675309/stars/%d/planets", FIRST_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Solar system with ID of %d not found", 8675309))));

        verify(apiService, times(1)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_InvalidSystemId_XXXStarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/8675309/stars/%d/planets", FIRST_STAR.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Solar system with ID of %d not found", 8675309))));

        verify(apiService, times(1)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_InvalidSystemId_XXXStarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get("/systems/8675309/stars/8675309/planets").accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_InvalidSystemId_XXXStarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(put("/systems/8675309/stars/8675309/planets").accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_ValidSystemId_MismatchStarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(SECOND_STAR.getId()));

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), SECOND_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Star with ID of %d not found", SECOND_STAR.getId()))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_ValidSystemId_MismatchStarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(SECOND_STAR.getId()));

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), SECOND_STAR.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Star with ID of %d not found", SECOND_STAR.getId()))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_ValidSystemId_MismatchStarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), SECOND_STAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_ValidSystemId_MismatchStarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(put(String.format("/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), SECOND_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_ValidSystemId_ValidStarId_EmptyPlanets_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, EMPTY_STAR);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), EMPTY_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(PLANET_COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.trident-api:planet", empty()))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), EMPTY_STAR.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:star", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d", FIRST_SYSTEM.getId(), EMPTY_STAR.getId()))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_ValidSystemId_ValidStarId_EmptyPlanets_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, EMPTY_STAR);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), EMPTY_STAR.getId())).accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchema(PLANET_COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.trident-api:planet", empty()))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), EMPTY_STAR.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:star", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d", FIRST_SYSTEM.getId(), EMPTY_STAR.getId()))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_ValidSystemId_ValidStarId_EmptyPlanets_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, EMPTY_STAR);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), EMPTY_STAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllPlanets_ValidSystemId_ValidStarId_EmptyPlanets_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, EMPTY_STAR);

        mockMvc.perform(put(String.format("/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), EMPTY_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_ValidStarId_ValidPlanetId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId(), FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(PLANET_SCHEMA)))
                .andExpect(jsonPath("$.id", is(FIRST_PLANET.getId())))
                .andExpect(jsonPath("$.name", is(FIRST_PLANET.getName())))
                .andExpect(jsonPath("$.mass", closeTo(FIRST_PLANET.getMass(), 0.0001)))
                .andExpect(jsonPath("$.radius", closeTo(FIRST_PLANET.getRadius(), 0.0001)))
                .andExpect(jsonPath("$.semimajorAxis", closeTo(FIRST_PLANET.getSemimajorAxis(), 0.0001)))
                .andExpect(jsonPath("$.eccentricity", closeTo(FIRST_PLANET.getEccentricity(), 0.0001)))
                .andExpect(jsonPath("$.inclination", closeTo(FIRST_PLANET.getInclination(), 0.0001)))
                .andExpect(jsonPath("$.longitudeOfAscendingNode", closeTo(FIRST_PLANET.getLongitudeOfAscendingNode(), 0.0001)))
                .andExpect(jsonPath("$.argumentOfPeriapsis", closeTo(FIRST_PLANET.getArgumentOfPeriapsis(), 0.0001)))
                .andExpect(jsonPath("$.trueAnomalyAtEpoch", closeTo(FIRST_PLANET.getTrueAnomalyAtEpoch(), 0.0001)))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        FIRST_PLANET.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:planets", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), FIRST_STAR.getId()))))
                .andExpect(jsonPath("$._links.trident-api:calendars", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars", FIRST_SYSTEM.getId(), FIRST_STAR.getId(), FIRST_PLANET.getId()))))
                .andDo(document("planet",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(TRIDENT_API_VALUE))),
                        responseFields(
                                fieldWithPath("id").description("Planet ID number."),
                                fieldWithPath("name").description("Planet name."),
                                fieldWithPath("mass").description("Planet mass, in standard planetary mass."),
                                fieldWithPath("radius").description("Planet radius, in standard planetary radius."),
                                fieldWithPath("semimajorAxis").description("Planet orbit semimajor axis, in astronomical units"),
                                fieldWithPath("eccentricity").description("Planet orbit eccentricity"),
                                fieldWithPath("inclination").description("Planet orbit inclination, in radians"),
                                fieldWithPath("longitudeOfAscendingNode").description("Planet longitude of ascending node, in radians"),
                                fieldWithPath("argumentOfPeriapsis").description("Planet argument of periapsis, in radians"),
                                fieldWithPath("trueAnomalyAtEpoch").description("Planet true anomaly at epoch, in radians"),
                                subsectionWithPath("_links").ignored()),
                        commonLinks.and(
                                linkWithRel("trident-api:planets").description("List of planet resources."),
                                linkWithRel("trident-api:calendars").description("List of calendar resources."))));

        verify(apiService, times(3)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_ValidStarId_ValidPlanetId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId(), FIRST_PLANET.getId())).accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchema(PLANET_SCHEMA)))
                .andExpect(jsonPath("$.id", is(FIRST_PLANET.getId())))
                .andExpect(jsonPath("$.name", is(FIRST_PLANET.getName())))
                .andExpect(jsonPath("$.mass", closeTo(FIRST_PLANET.getMass(), 0.0001)))
                .andExpect(jsonPath("$.radius", closeTo(FIRST_PLANET.getRadius(), 0.0001)))
                .andExpect(jsonPath("$.semimajorAxis", closeTo(FIRST_PLANET.getSemimajorAxis(), 0.0001)))
                .andExpect(jsonPath("$.eccentricity", closeTo(FIRST_PLANET.getEccentricity(), 0.0001)))
                .andExpect(jsonPath("$.inclination", closeTo(FIRST_PLANET.getInclination(), 0.0001)))
                .andExpect(jsonPath("$.longitudeOfAscendingNode", closeTo(FIRST_PLANET.getLongitudeOfAscendingNode(), 0.0001)))
                .andExpect(jsonPath("$.argumentOfPeriapsis", closeTo(FIRST_PLANET.getArgumentOfPeriapsis(), 0.0001)))
                .andExpect(jsonPath("$.trueAnomalyAtEpoch", closeTo(FIRST_PLANET.getTrueAnomalyAtEpoch(), 0.0001)))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        FIRST_PLANET.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:planets", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets", FIRST_SYSTEM.getId(), FIRST_STAR.getId()))))
                .andExpect(jsonPath("$._links.trident-api:calendars", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars", FIRST_SYSTEM.getId(), FIRST_STAR.getId(), FIRST_PLANET.getId()))));

        verify(apiService, times(3)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_ValidStarId_ValidPlanetId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId(), FIRST_PLANET.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_ValidStarId_ValidPlanetId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET);

        mockMvc.perform(put(String.format("/systems/%d/stars/%d/planets/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId(), FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_ValidStarId_InvalidPlanetId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets/8675309", FIRST_SYSTEM.getId(), FIRST_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Planet with ID of %d not found", 8675309))));

        verify(apiService, times(3)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_ValidStarId_InvalidPlanetId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets/8675309", FIRST_SYSTEM.getId(), FIRST_STAR.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Planet with ID of %d not found", 8675309))));

        verify(apiService, times(3)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_ValidStarId_InvalidPlanetId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets/8675309", FIRST_SYSTEM.getId(), FIRST_STAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_ValidStarId_InvalidPlanetId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(8675309));

        mockMvc.perform(put(String.format("/systems/%d/stars/%d/planets/8675309", FIRST_SYSTEM.getId(), FIRST_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_InvalidStarId_XXXPlanetId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/%d/stars/8675309/planets/%d", FIRST_SYSTEM.getId(), FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Star with ID of %d not found", 8675309))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_InvalidStarId_XXXPlanetId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/%d/stars/8675309/planets/%d", FIRST_SYSTEM.getId(), FIRST_PLANET.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Star with ID of %d not found", 8675309))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_InvalidStarId_XXXPlanetId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/%d/stars/8675309/planets/%d", FIRST_SYSTEM.getId(), FIRST_PLANET.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_InvalidStarId_XXXPlanetId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(put(String.format("/systems/%d/stars/8675309/planets/%d", FIRST_SYSTEM.getId(), FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_InvalidSystemId_XXXStarId_XXXPlanetId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/8675309/stars/%d/planets/%d", FIRST_STAR.getId(), FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Solar system with ID of %d not found", 8675309))));

        verify(apiService, times(1)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_InvalidSystemId_XXXStarId_XXXPlanetId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/8675309/stars/%d/planets/%d", FIRST_STAR.getId(), FIRST_PLANET.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Solar system with ID of %d not found", 8675309))));

        verify(apiService, times(1)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_InvalidSystemId_XXXStarId_XXXPlanetId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/8675309/stars/%d/planets/%d", FIRST_STAR.getId(), FIRST_PLANET.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_InvalidSystemId_XXXStarId_XXXPlanetId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(put(String.format("/systems/8675309/stars/%d/planets/%d", FIRST_STAR.getId(), FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_ValidStarId_MismatchPlanetId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(THIRD_PLANET.getId()));

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId(), THIRD_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Planet with ID of %d not found", THIRD_PLANET.getId()))));

        verify(apiService, times(3)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_ValidStarId_MismatchPlanetId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(THIRD_PLANET.getId()));

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId(), THIRD_PLANET.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Planet with ID of %d not found", THIRD_PLANET.getId()))));

        verify(apiService, times(3)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_ValidStarId_MismatchPlanetId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(THIRD_PLANET.getId()));

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId(), THIRD_PLANET.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_ValidStarId_MismatchPlanetId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(THIRD_PLANET.getId()));

        mockMvc.perform(put(String.format("/systems/%d/stars/%d/planets/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId(), THIRD_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_MismatchStarId_XXXPlanetId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(SECOND_STAR.getId()));

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets/%d", FIRST_SYSTEM.getId(), SECOND_STAR.getId(), THIRD_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Star with ID of %d not found", SECOND_STAR.getId()))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_MismatchStarId_XXXPlanetId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(THIRD_PLANET.getId()));

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId(), THIRD_PLANET.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Planet with ID of %d not found", THIRD_PLANET.getId()))));

        verify(apiService, times(3)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_MismatchStarId_XXXPlanetId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(SECOND_STAR.getId()));

        mockMvc.perform(get(String.format("/systems/%d/stars/%d/planets/%d", FIRST_SYSTEM.getId(), SECOND_STAR.getId(), FIRST_PLANET.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSinglePlanet_ValidSystemId_MismatchStarId_XXXPlanetId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(SECOND_STAR.getId()));

        mockMvc.perform(put(String.format("/systems/%d/stars/%d/planets/%d", FIRST_SYSTEM.getId(), SECOND_STAR.getId(), FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }
}