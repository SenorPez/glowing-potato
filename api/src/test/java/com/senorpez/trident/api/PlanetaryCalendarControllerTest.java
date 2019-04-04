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

public class PlanetaryCalendarControllerTest {
    private MockMvc mockMvc;
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "invalid+json", UTF_8);
    private static final ClassLoader CLASS_LOADER = PlanetaryCalendarControllerTest.class.getClassLoader();
    private static InputStream CALENDAR_SCHEMA;
    private static InputStream CALENDAR_COLLECTION_SCHEMA;
    private static InputStream ERROR_SCHEMA;

    private static final PlanetaryCalendar FIRST_CALENDAR = new PlanetaryCalendarBuilder()
            .setId(123456789)
            .setName("Laganese Calendar")
            .setStandardHoursPerDay(36.3624863f)
            .setEpochOffset(-72.27522481178462f)
            .build();

    private static final PlanetaryCalendar SECOND_CALENDAR = new PlanetaryCalendarBuilder()
            .setId(987654321)
            .setName("Normie")
            .setStandardHoursPerDay(24f)
            .setEpochOffset(0f)
            .build();

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
            .setCalendars(new HashSet<>(Arrays.asList(
                    FIRST_CALENDAR,
                    SECOND_CALENDAR)))
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

    private static final Star FIRST_STAR = new StarBuilder()
            .setId(11)
            .setName("1 Eta Veneris")
            .setMass((float) 0.75)
            .setPlanets(new HashSet<>(Collections.singletonList(FIRST_PLANET)))
            .build();

    private static final Star SECOND_STAR = new StarBuilder()
            .setId(21)
            .setName("Sol")
            .setMass((float) 1)
            .setPlanets(new HashSet<>(Collections.singletonList(SECOND_PLANET)))
            .build();

    private static final SolarSystem FIRST_SYSTEM = new SolarSystemBuilder()
            .setId(1)
            .setName("Eta Veneris")
            .setStars(new HashSet<>(Collections.singletonList(FIRST_STAR)))
            .build();

    private static final Planet EMPTY_PLANET = new PlanetBuilder()
            .setId(31)
            .setName("Empty")
            .setCalendars(null)
            .build();

    @InjectMocks
    PlanetaryCalendarController planetaryCalendarController;

    @Mock
    private APIService apiService;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Before
    public void setUp() {
        CALENDAR_SCHEMA = CLASS_LOADER.getResourceAsStream("calendar.schema.json");
        CALENDAR_COLLECTION_SCHEMA = CLASS_LOADER.getResourceAsStream("calendars.schema.json");
        ERROR_SCHEMA = CLASS_LOADER.getResourceAsStream("error.schema.json");
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new PlanetaryCalendarController(apiService))
                .setMessageConverters(HALMessageConverter.getConverter(Collections.singletonList(ALL)))
                .setControllerAdvice(new APIExceptionHandler())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void GetAllCalendars_ValidSystemId_ValidStarId_ValidPlanetId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET);

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(CALENDAR_COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.trident-api:calendar", hasItem(
                        allOf(
                                hasEntry("id", (Object) FIRST_CALENDAR.getId()),
                                hasEntry("name", (Object) FIRST_CALENDAR.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format(
                                                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                                                        FIRST_SYSTEM.getId(),
                                                        FIRST_STAR.getId(),
                                                        FIRST_PLANET.getId(),
                                                        FIRST_CALENDAR.getId()))))))))
                .andExpect(jsonPath("$._embedded.trident-api:calendar", hasItem(
                        allOf(
                                hasEntry("id", (Object) SECOND_CALENDAR.getId()),
                                hasEntry("name", (Object) SECOND_CALENDAR.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format(
                                                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                                                        FIRST_SYSTEM.getId(),
                                                        FIRST_STAR.getId(),
                                                        FIRST_PLANET.getId(),
                                                        SECOND_CALENDAR.getId()))))))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        FIRST_PLANET.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:planet", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        FIRST_PLANET.getId()))))
                .andDo(document("calendars",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(TRIDENT_API_VALUE))),
                        responseFields(
                                fieldWithPath("_embedded.trident-api:calendar").description("Calendar resource."),
                                fieldWithPath("_embedded.trident-api:calendar[].id").description("Calendar ID number."),
                                fieldWithPath("_embedded.trident-api:calendar[].name").description("Calendar name."),
                                subsectionWithPath("_links").ignored(),
                                subsectionWithPath("_embedded.trident-api:calendar[]._links").ignored()),
                        commonLinks.and(
                                linkWithRel("trident-api:planet").description("Planet resource."))));

        verify(apiService, times(3)).findOne(any(), any(), any());
    }

    @Test
    public void GetAllCalendars_ValidSystemId_ValidStarId_ValidPlanetId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET);

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchema(CALENDAR_COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.trident-api:calendar", hasItem(
                        allOf(
                                hasEntry("id", (Object) FIRST_CALENDAR.getId()),
                                hasEntry("name", (Object) FIRST_CALENDAR.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format(
                                                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                                                        FIRST_SYSTEM.getId(),
                                                        FIRST_STAR.getId(),
                                                        FIRST_PLANET.getId(),
                                                        FIRST_CALENDAR.getId()))))))))
                .andExpect(jsonPath("$._embedded.trident-api:calendar", hasItem(
                        allOf(
                                hasEntry("id", (Object) SECOND_CALENDAR.getId()),
                                hasEntry("name", (Object) SECOND_CALENDAR.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format(
                                                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                                                        FIRST_SYSTEM.getId(),
                                                        FIRST_STAR.getId(),
                                                        FIRST_PLANET.getId(),
                                                        SECOND_CALENDAR.getId()))))))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        FIRST_PLANET.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:planet", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        FIRST_PLANET.getId()))));

        verify(apiService, times(3)).findOne(any(), any(), any());
    }

    @Test
    public void GetAllCalendars_ValidSystemId_ValidStarId_ValidPlanetId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET);

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllCalendars_ValidSystemId_ValidStarId_ValidPlanetId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET);

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllCalendars_ValidSystemId_ValidStarId_InvalidPlanetId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/8675309/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId())).accept(TRIDENT_API))
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
    public void GetAllCalendars_ValidSystemId_ValidStarId_InvalidPlanetId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/8675309/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId())).accept(FALLBACK))
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
    public void GetAllCalendars_ValidSystemId_ValidStarId_InvalidPlanetId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllCalendars_ValidSystemId_ValidStarId_InvalidPlanetId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(8675309));

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllCalendars_ValidSystemId_InvalidStarId_XXXPlanetId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/8675309/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
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
    public void GetAllCalendars_ValidSystemId_InvalidStarId_XXXPlanetId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/8675309/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_CALENDAR.getId())).accept(FALLBACK))
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
    public void GetAllCalendars_ValidSystemId_InvalidStarId_XXXPlanetId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllCalendars_ValidSystemId_InvalidStarId_XXXPlanetId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllCalendars_InvalidSystemId_XXXStarId_XXXPlanetId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "/systems/8675309/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
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
    public void GetAllCalendars_InvalidSystemId_XXXStarId_XXXPlanetId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "/systems/8675309/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_CALENDAR.getId())).accept(FALLBACK))
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
    public void GetAllCalendars_InvalidSystemId_XXXStarId_XXXPlanetId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllCalendars_InvalidSystemId_XXXStarId_XXXPlanetId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllCalendars_ValidSystemId_ValidStarId_MismatchPlanetId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(SECOND_PLANET.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                SECOND_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Planet with ID of %d not found", SECOND_PLANET.getId()))));

        verify(apiService, times(3)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetAllCalendars_ValidSystemId_ValidStarId_MismatchPlanetId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(SECOND_PLANET.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                SECOND_PLANET.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Planet with ID of %d not found", SECOND_PLANET.getId()))));

        verify(apiService, times(3)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetAllCalendars_ValidSystemId_ValidStarId_MismatchPlanetId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(SECOND_PLANET.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllCalendars_ValidSystemId_ValidStarId_MismatchPlanetId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(SECOND_PLANET.getId()));

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }


    @Test
    public void GetAllCalendars_ValidSystemId_MismatchStarId_XXXPlanetId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(SECOND_STAR.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                SECOND_STAR.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
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
    public void GetAllCalendars_ValidSystemId_MismatchStarId_XXXPlanetId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(SECOND_STAR.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                SECOND_STAR.getId(),
                FIRST_CALENDAR.getId())).accept(FALLBACK))
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
    public void GetAllCalendars_ValidSystemId_MismatchStarId_XXXPlanetId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(SECOND_STAR.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllCalendars_ValidSystemId_MismatchStarId_XXXPlanetId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(SECOND_STAR.getId()));

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllCalendars_ValidSystemId_ValidStarId_ValidPlanetId_EmptyCalendars_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, EMPTY_PLANET);

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                EMPTY_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(CALENDAR_COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.trident-api:calendar", empty()))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        EMPTY_PLANET.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:planet", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        EMPTY_PLANET.getId()))));

        verify(apiService, times(3)).findOne(any(), any(), any());
    }

    @Test
    public void GetAllCalendars_ValidSystemId_ValidStarId_ValidPlanetId_EmptyCalendars_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, EMPTY_PLANET);

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                EMPTY_PLANET.getId())).accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchema(CALENDAR_COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.trident-api:calendar", empty()))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        EMPTY_PLANET.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:planet", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        EMPTY_PLANET.getId()))));

        verify(apiService, times(3)).findOne(any(), any(), any());
    }

    @Test
    public void GetAllCalendars_ValidSystemId_ValidStarId_ValidPlanetId_EmptyCalendars_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, EMPTY_PLANET);

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllCalendars_ValidSystemId_ValidStarId_ValidPlanetId_EmptyCalendars_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, EMPTY_PLANET);

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_ValidPlanetId_ValidCalendarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET, FIRST_CALENDAR);

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(CALENDAR_SCHEMA)))
                .andExpect(jsonPath("$.id", is(FIRST_CALENDAR.getId())))
                .andExpect(jsonPath("$.name", is(FIRST_CALENDAR.getName())))
                .andExpect(jsonPath("$.standardHoursPerDay", closeTo(FIRST_CALENDAR.getStandardHoursPerDay(), 0.0001)))
                .andExpect(jsonPath("$.epochOffset", closeTo(FIRST_CALENDAR.getEpochOffset(), 0.0001)))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        FIRST_PLANET.getId(),
                        FIRST_CALENDAR.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:calendars", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        FIRST_PLANET.getId()))))
                .andDo(document("calendar",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(TRIDENT_API_VALUE))),
                        responseFields(
                                fieldWithPath("id").description("Calendar ID number."),
                                fieldWithPath("name").description("Calendar name."),
                                fieldWithPath("standardHoursPerDay").description("Standard hours per local day."),
                                fieldWithPath("epochOffset").description("Offset between standard epoch (J2000) and local epoch."),
                                subsectionWithPath("_links").ignored()),
                        commonLinks.and(
                                linkWithRel("trident-api:calendars").description("List of calendar resources."),
                                linkWithRel("trident-api:festivalYear").description("Given a local year, returns if a festival year."))));

        verify(apiService, times(4)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_ValidPlanetId_ValidCalendarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET, FIRST_CALENDAR);

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchema(CALENDAR_SCHEMA)))
                .andExpect(jsonPath("$.id", is(FIRST_CALENDAR.getId())))
                .andExpect(jsonPath("$.name", is(FIRST_CALENDAR.getName())))
                .andExpect(jsonPath("$.standardHoursPerDay", closeTo(FIRST_CALENDAR.getStandardHoursPerDay(), 0.0001)))
                .andExpect(jsonPath("$.epochOffset", closeTo(FIRST_CALENDAR.getEpochOffset(), 0.0001)))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        FIRST_PLANET.getId(),
                        FIRST_CALENDAR.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:calendars", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        FIRST_PLANET.getId()))));

        verify(apiService, times(4)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_ValidPlanetId_ValidCalendarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET, FIRST_CALENDAR);

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_ValidPlanetId_ValidCalendarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET, FIRST_CALENDAR);

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }


    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_ValidPlanetId_InvalidCalendarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET).thenThrow(new PlanetaryCalendarNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/8675309",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Calendar with ID of %d not found", 8675309))));

        verify(apiService, times(4)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_ValidPlanetId_InvalidCalendarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET).thenThrow(new PlanetaryCalendarNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/8675309",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Calendar with ID of %d not found", 8675309))));

        verify(apiService, times(4)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_ValidPlanetId_InvalidCalendarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET).thenThrow(new PlanetaryCalendarNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_ValidPlanetId_InvalidCalendarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET).thenThrow(new PlanetaryCalendarNotFoundException(8675309));

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_InvalidPlanetId_XXXCalendarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/8675309/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
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
    public void GetSingleCalendar_ValidSystemId_ValidStarId_InvalidPlanetId_XXXCalendarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/8675309/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_CALENDAR.getId())).accept(FALLBACK))
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
    public void GetSingleCalendar_ValidSystemId_ValidStarId_InvalidPlanetId_XXXCalendarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_InvalidPlanetId_XXXCalendarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(8675309));

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_InvalidStarId_XXXPlanetId_XXXCalendarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/8675309/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
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
    public void GetSingleCalendar_ValidSystemId_InvalidStarId_XXXPlanetId_XXXCalendarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/8675309/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(FALLBACK))
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
    public void GetSingleCalendar_ValidSystemId_InvalidStarId_XXXPlanetId_XXXCalendarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_InvalidStarId_XXXPlanetId_XXXCalendarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_InvalidSystemId_XXXStarId_XXXPlanetId_XXXCalendarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "/systems/8675309/stars/%d/planets/%d/calendars/%d",
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
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
    public void GetSingleCalendar_InvalidSystemId_XXXStarId_XXXPlanetId_XXXCalendarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "/systems/8675309/stars/%d/planets/%d/calendars/%d",
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(FALLBACK))
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
    public void GetSingleCalendar_InvalidSystemId_XXXStarId_XXXPlanetId_XXXCalendarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_InvalidSystemId_XXXStarId_XXXPlanetId_XXXCalendarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_ValidPlanetId_MismatchCalendarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET).thenThrow(new PlanetaryCalendarNotFoundException(SECOND_CALENDAR.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                SECOND_CALENDAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Calendar with ID of %d not found", SECOND_CALENDAR.getId()))));

        verify(apiService, times(4)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_ValidPlanetId_MismatchCalendarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET).thenThrow(new PlanetaryCalendarNotFoundException(SECOND_CALENDAR.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                SECOND_CALENDAR.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Calendar with ID of %d not found", SECOND_CALENDAR.getId()))));

        verify(apiService, times(4)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_ValidPlanetId_MismatchCalendarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET).thenThrow(new PlanetaryCalendarNotFoundException(SECOND_CALENDAR.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_ValidPlanetId_MismatchCalendarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET).thenThrow(new PlanetaryCalendarNotFoundException(SECOND_CALENDAR.getId()));

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_MismatchPlanetId_XXXCalendarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(SECOND_PLANET.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                SECOND_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Planet with ID of %d not found", SECOND_PLANET.getId()))));

        verify(apiService, times(3)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_MismatchPlanetId_XXXCalendarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(SECOND_PLANET.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                SECOND_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Planet with ID of %d not found", SECOND_PLANET.getId()))));

        verify(apiService, times(3)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_MismatchPlanetId_XXXCalendarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(SECOND_PLANET.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_ValidStarId_MismatchPlanetId_XXXCalendarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR).thenThrow(new PlanetNotFoundException(SECOND_PLANET.getId()));

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_MismatchStarId_XXXPlanetId_XXXCalendarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(SECOND_STAR.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                SECOND_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
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
    public void GetSingleCalendar_ValidSystemId_MismatchStarId_XXXPlanetId_XXXCalendarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(SECOND_STAR.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                SECOND_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(FALLBACK))
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
    public void GetSingleCalendar_ValidSystemId_MismatchStarId_XXXPlanetId_XXXCalendarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(SECOND_STAR.getId()));

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetSingleCalendar_ValidSystemId_MismatchStarId_XXXPlanetId_XXXCalendarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(SECOND_STAR.getId()));

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }
}
