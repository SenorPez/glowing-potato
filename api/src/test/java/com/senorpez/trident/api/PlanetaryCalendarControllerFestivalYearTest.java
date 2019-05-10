package com.senorpez.trident.api;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static com.senorpez.trident.api.SupportedMediaTypes.FALLBACK;
import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(Parameterized.class)
public class PlanetaryCalendarControllerFestivalYearTest {
    private MockMvc mockMvc;
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "invalid+json", UTF_8);
    private static final ClassLoader CLASS_LOADER = PlanetaryCalendarControllerFestivalYearTest.class.getClassLoader();
    private static InputStream FESTIVAL_SCHEMA;
    private static InputStream ERROR_SCHEMA;

    private final int localYear;
    private final boolean festivalYear;

    private static final PlanetaryCalendar FIRST_CALENDAR = new PlanetaryCalendarBuilder()
            .setId(123456789)
            .setName("Laganese Calendar")
            .setStandardHoursPerDay(36.3624863f)
            .setEpochOffset(-72.27522481178462f)
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
            .setCalendars(new HashSet<>(Collections.singletonList(FIRST_CALENDAR)))
            .build();

    private static final Star FIRST_STAR = new StarBuilder()
            .setId(11)
            .setName("1 Eta Veneris")
            .setMass((float) 0.75)
            .setPlanets(new HashSet<>(Collections.singletonList(FIRST_PLANET)))
            .build();

    private static final SolarSystem FIRST_SYSTEM = new SolarSystemBuilder()
            .setId(1)
            .setName("Eta Veneris")
            .setStars(new HashSet<>(Collections.singletonList(FIRST_STAR)))
            .build();


    @InjectMocks
    PlanetaryCalendarController planetaryCalendarController;

    @Mock
    private APIService apiService;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Before
    public void setUp() {
        FESTIVAL_SCHEMA = CLASS_LOADER.getResourceAsStream("festival.schema.json");
        ERROR_SCHEMA = CLASS_LOADER.getResourceAsStream("error.schema.json");
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new FestivalYearController())
                .setMessageConverters(HALMessageConverter.getConverter(Collections.singletonList(ALL)))
                .setControllerAdvice(new APIExceptionHandler())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Parameterized.Parameters(name = "year: {0}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {47, false},
                {48, true},
                {49, false},
                {50, false},
                {51, false},
                {52, false},
                {53, false},
                {54, true},
                {55, false},
        });
    }

    public PlanetaryCalendarControllerFestivalYearTest(int localYear, boolean festivalYear) {
        this.localYear = localYear;
        this.festivalYear = festivalYear;
    }

    @Test
    public void GetFestivalYear_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET, FIRST_CALENDAR);

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d/festivalYear/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId(),
                localYear)).accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(FESTIVAL_SCHEMA)))
                .andExpect(jsonPath("$.isFestivalYear", is(festivalYear)))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d/festivalYear/%d",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        FIRST_PLANET.getId(),
                        FIRST_CALENDAR.getId(),
                        localYear))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:calendar", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        FIRST_PLANET.getId(),
                        FIRST_CALENDAR.getId()))));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetFestivalYear_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET, FIRST_CALENDAR);

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d/festivalYear/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId(),
                localYear)).accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchema(FESTIVAL_SCHEMA)))
                .andExpect(jsonPath("$.isFestivalYear", is(festivalYear)))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d/festivalYear/%d",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        FIRST_PLANET.getId(),
                        FIRST_CALENDAR.getId(),
                        localYear))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:calendar", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId(),
                        FIRST_PLANET.getId(),
                        FIRST_CALENDAR.getId()))));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetFestivalYear_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET, FIRST_CALENDAR);

        mockMvc.perform(get(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d/festivalYear/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId(),
                localYear)).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetFestivalYear_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR, FIRST_PLANET, FIRST_CALENDAR);

        mockMvc.perform(put(String.format(
                "http://localhost:8080/systems/%d/stars/%d/planets/%d/calendars/%d/festivalYear/%d",
                FIRST_SYSTEM.getId(),
                FIRST_STAR.getId(),
                FIRST_PLANET.getId(),
                FIRST_CALENDAR.getId(),
                localYear)).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }
}
