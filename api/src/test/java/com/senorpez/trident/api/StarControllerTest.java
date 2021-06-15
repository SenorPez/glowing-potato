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
import static org.springframework.http.MediaType.*;
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

public class StarControllerTest {
    private MockMvc mockMvc;
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "vnd.senorpez.trident.v0+json", UTF_8);
    private static final ClassLoader CLASS_LOADER = StarControllerTest.class.getClassLoader();
    private static InputStream STAR_SCHEMA;
    private static InputStream STAR_COLLECTION_SCHEMA;
    private static InputStream ERROR_SCHEMA;

    private static final Star FIRST_STAR = new StarBuilder()
            .setId(11)
            .setName("1 Eta Veneris")
            .setMass((float) 0.75)
            .build();

    private static final Star SECOND_STAR = new StarBuilder()
            .setId(12)
            .setName("2 Eta Veneris")
            .setMass((float) 0.75)
            .setSemimajorAxis((float) 70)
            .setEccentricty((float) 0.5)
            .setInclination((float) 0.00627394)
            .setLongitudeofAscendingNode((float) 4.82101)
            .setArgumentOfPeriapsis((float) 2.95583)
            .setTrueAnomalyAtEpoch((float) 6.01675)
            .build();

    private static final Star THIRD_STAR = new StarBuilder()
            .setId(21)
            .setName("Sol")
            .setMass((float) 1.0)
            .build();

    private static final SolarSystem FIRST_SYSTEM = new SolarSystemBuilder()
            .setId(1)
            .setName("Eta Veneris")
            .setStars(new HashSet<>(Arrays.asList(
                    FIRST_STAR,
                    SECOND_STAR)))
            .build();

    private static final SolarSystem SECOND_SYSTEM = new SolarSystemBuilder()
            .setId(2)
            .setName("Sol")
            .setStars(new HashSet<>(Collections.singletonList(
                    THIRD_STAR)))
            .build();

    @InjectMocks
    StarController starController;

    @Mock
    private APIService apiService;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Before
    public void setUp() {
        STAR_SCHEMA = CLASS_LOADER.getResourceAsStream("star.schema.json");
        STAR_COLLECTION_SCHEMA = CLASS_LOADER.getResourceAsStream("stars.schema.json");
        ERROR_SCHEMA = CLASS_LOADER.getResourceAsStream("error.schema.json");
        MockitoAnnotations.initMocks(this);
        SupportedMediaTypes supportedMediaTypes = new SupportedMediaTypes();

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new StarController(apiService))
                .setMessageConverters(supportedMediaTypes.getConverter(Collections.singletonList(ALL)))
                .setControllerAdvice(new APIExceptionHandler())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void GetAllStars_ValidSystemId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM);

        mockMvc.perform(get(String.format("/systems/%d/stars/", FIRST_SYSTEM.getId())).accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(STAR_COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.trident-api:star", hasItem(
                        allOf(
                                hasEntry("id", (Object) FIRST_STAR.getId()),
                                hasEntry("name", (Object) FIRST_STAR.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format(
                                                        "http://localhost:8080/systems/%d/stars/%d",
                                                        FIRST_SYSTEM.getId(),
                                                        FIRST_STAR.getId()))))))))
                .andExpect(jsonPath("$._embedded.trident-api:star", hasItem(
                        allOf(
                                hasEntry("id", (Object) SECOND_STAR.getId()),
                                hasEntry("name", (Object) SECOND_STAR.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format(
                                                        "http://localhost:8080/systems/%d/stars/%d",
                                                        FIRST_SYSTEM.getId(),
                                                        SECOND_STAR.getId()))))))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars", FIRST_SYSTEM.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:system", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d", FIRST_SYSTEM.getId()))))
                .andDo(document("stars",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(TRIDENT_API_VALUE))),
                        responseFields(
                                fieldWithPath("_embedded.trident-api:star").description("Star resource."),
                                fieldWithPath("_embedded.trident-api:star[].id").description("Star ID number."),
                                fieldWithPath("_embedded.trident-api:star[].name").description("Star name."),
                                subsectionWithPath("_links").ignored(),
                                subsectionWithPath("_embedded.trident-api:star[]._links").ignored()),
                        commonLinks.and(
                                linkWithRel("trident-api:system").description("Solar system resource."))));

        verify(apiService, times(1)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);

    }

    @Test
    public void GetAllStars_ValidSystemId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM);

        mockMvc.perform(get(String.format("/systems/%d/stars/", FIRST_SYSTEM.getId())).accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchema(STAR_COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.trident-api:star", hasItem(
                        allOf(
                                hasEntry("id", (Object) FIRST_STAR.getId()),
                                hasEntry("name", (Object) FIRST_STAR.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format(
                                                        "http://localhost:8080/systems/%d/stars/%d",
                                                        FIRST_SYSTEM.getId(),
                                                        FIRST_STAR.getId()))))))))
                .andExpect(jsonPath("$._embedded.trident-api:star", hasItem(
                        allOf(
                                hasEntry("id", (Object) SECOND_STAR.getId()),
                                hasEntry("name", (Object) SECOND_STAR.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format(
                                                        "http://localhost:8080/systems/%d/stars/%d",
                                                        FIRST_SYSTEM.getId(),
                                                        SECOND_STAR.getId()))))))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars", FIRST_SYSTEM.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:system", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d", FIRST_SYSTEM.getId()))));

        verify(apiService, times(1)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetAllStars_ValidSystemId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM);

        mockMvc.perform(get(String.format("/systems/%d/stars/", FIRST_SYSTEM.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyNoInteractions(apiService);
    }

    @Test
    public void GetAllStars_ValidSystemId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM);

        mockMvc.perform(put(String.format("/systems/%d/stars/", FIRST_SYSTEM.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyNoInteractions(apiService);
    }

    @Test
    public void GetAllStars_InvalidSystemId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get("/systems/8675309/stars/").accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Solar system with ID of %d not found", 8675309))));

        verify(apiService, times(1)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetAllStars_InvalidSystemId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get("/systems/8675309/stars/").accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Solar system with ID of %d not found", 8675309))));

        verify(apiService, times(1)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetAllStars_InvalidSystemId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get("/systems/8675309/stars/").accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyNoInteractions(apiService);
    }

    @Test
    public void GetAllStars_InvalidSystemId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(put("/systems/8675309/stars").accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyNoInteractions(apiService);
    }

    @Test
    public void GetSingleStar_ValidSystemId_ValidStarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(STAR_SCHEMA)))
                .andExpect(jsonPath("$.id", is(FIRST_STAR.getId())))
                .andExpect(jsonPath("$.name", is(FIRST_STAR.getName())))
                .andExpect(jsonPath("$.mass", closeTo(FIRST_STAR.getMass(), 0.001)))
                .andExpect(jsonPath("$.semimajorAxis", nullValue()))
                .andExpect(jsonPath("$.eccentricity", nullValue()))
                .andExpect(jsonPath("$.inclination", nullValue()))
                .andExpect(jsonPath("$.longitudeOfAscendingNode", nullValue()))
                .andExpect(jsonPath("$.argumentOfPeriapsis", nullValue()))
                .andExpect(jsonPath("$.trueAnomalyAtEpoch", nullValue()))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:stars", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars", FIRST_SYSTEM.getId()))))
                .andDo(document("star",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(TRIDENT_API_VALUE))),
                        responseFields(
                                fieldWithPath("id").description("Star ID number."),
                                fieldWithPath("name").description("Star name."),
                                fieldWithPath("mass").description("Star mass."),
                                fieldWithPath("semimajorAxis").description("Orbit semimajor axis, in astronomical units, secondary stars only."),
                                fieldWithPath("eccentricity").description("Orbit eccentricity, secondary stars only."),
                                fieldWithPath("inclination").description("Orbit inclination, in radians, secondary stars only."),
                                fieldWithPath("longitudeOfAscendingNode").description("Longitude of ascending node, in radians, secondary stars only."),
                                fieldWithPath("argumentOfPeriapsis").description("Argument of periapsis, in radians, secondary stars only."),
                                fieldWithPath("trueAnomalyAtEpoch").description("True anomaly at epoch, in radians, secondary stars only."),
                                subsectionWithPath("_links").ignored()),
                        commonLinks.and(
                                linkWithRel("trident-api:stars").description("List of star resources."),
                                linkWithRel("trident-api:planets").description("List of planet resources."))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleStar_ValidSystemId_ValidStarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId())).accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchema(STAR_SCHEMA)))
                .andExpect(jsonPath("$.id", is(FIRST_STAR.getId())))
                .andExpect(jsonPath("$.name", is(FIRST_STAR.getName())))
                .andExpect(jsonPath("$.mass", closeTo(FIRST_STAR.getMass(), 0.001)))
                .andExpect(jsonPath("$.semimajorAxis", nullValue()))
                .andExpect(jsonPath("$.eccentricity", nullValue()))
                .andExpect(jsonPath("$.inclination", nullValue()))
                .andExpect(jsonPath("$.longitudeOfAscendingNode", nullValue()))
                .andExpect(jsonPath("$.argumentOfPeriapsis", nullValue()))
                .andExpect(jsonPath("$.trueAnomalyAtEpoch", nullValue()))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d",
                        FIRST_SYSTEM.getId(),
                        FIRST_STAR.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:stars", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars", FIRST_SYSTEM.getId()))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleStar_ValidSystemId_ValidStarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyNoInteractions(apiService);
    }

    @Test
    public void GetSingleStar_ValidSystemId_ValidStarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, FIRST_STAR);

        mockMvc.perform(put(String.format("/systems/%d/stars/%d", FIRST_SYSTEM.getId(), FIRST_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyNoInteractions(apiService);
    }

    @Test
    public void GetSingleStar_ValidSystemId_InvalidStarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/%d/stars/8675309", FIRST_SYSTEM.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Star with ID of %d not found", 8675309))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleStar_ValidSystemId_InvalidStarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/%d/stars/8675309", FIRST_SYSTEM.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Star with ID of %d not found", 8675309))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleStar_ValidSystemId_InvalidStarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/%d/stars/8675309", FIRST_SYSTEM.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyNoInteractions(apiService);
    }

    @Test
    public void GetSingleStar_ValidSystemId_InvalidStarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM).thenThrow(new StarNotFoundException(8675309));

        mockMvc.perform(put(String.format("/systems/%d/stars/8675309", FIRST_SYSTEM.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyNoInteractions(apiService);
    }

    @Test
    public void GetSingleStar_InvalidSystemId_XXXStarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/8675309/stars/%d", FIRST_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Solar system with ID of %d not found", 8675309))));

        verify(apiService, times(1)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleStar_InvalidSystemId_XXXStarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/8675309/stars/%d", FIRST_STAR.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Solar system with ID of %d not found", 8675309))));

        verify(apiService, times(1)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleStar_InvalidSystemId_XXXStarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(get(String.format("/systems/8675309/stars/%d", FIRST_STAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyNoInteractions(apiService);
    }

    @Test
    public void GetSingleStar_InvalidSystemId_XXXStarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenThrow(new SolarSystemNotFoundException(8675309));

        mockMvc.perform(put(String.format("/systems/8675309/stars/%d", FIRST_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyNoInteractions(apiService);
    }

    @Test
    public void GetSingleStar_ValidSystemId_MismatchStarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(SECOND_SYSTEM).thenThrow(new StarNotFoundException(FIRST_STAR.getId()));

        mockMvc.perform(get(String.format("/systems/%d/stars/%d", SECOND_SYSTEM.getId(), FIRST_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Star with ID of %d not found", FIRST_STAR.getId()))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleStar_ValidSystemId_MismatchStarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(SECOND_SYSTEM).thenThrow(new StarNotFoundException(FIRST_STAR.getId()));

        mockMvc.perform(get(String.format("/systems/%d/stars/%d", SECOND_SYSTEM.getId(), FIRST_STAR.getId())).accept(FALLBACK))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Star with ID of %d not found", FIRST_STAR.getId()))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSingleStar_ValidSystemId_MismatchStarId_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(SECOND_SYSTEM, FIRST_STAR);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d", SECOND_SYSTEM.getId(), FIRST_STAR.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/vnd.senorpez.trident.v1+json;charset=UTF-8\"")));

        verifyNoInteractions(apiService);
    }

    @Test
    public void GetSingleStar_ValidSystemId_MismatchStarId_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(SECOND_SYSTEM, FIRST_STAR);

        mockMvc.perform(put(String.format("/systems/%d/stars/%d", SECOND_SYSTEM.getId(), FIRST_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyNoInteractions(apiService);
    }

    @Test
    public void GetSecondaryStar_ValidSystemId_ValidStarId_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, SECOND_STAR);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d", FIRST_SYSTEM.getId(), SECOND_STAR.getId())).accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(STAR_SCHEMA)))
                .andExpect(jsonPath("$.id", is(SECOND_STAR.getId())))
                .andExpect(jsonPath("$.name", is(SECOND_STAR.getName())))
                .andExpect(jsonPath("$.mass", closeTo(SECOND_STAR.getMass(), 0.001)))
                .andExpect(jsonPath("$.semimajorAxis", closeTo((double) SECOND_STAR.getSemimajorAxis(), 0.001)))
                .andExpect(jsonPath("$.eccentricity", closeTo((double) SECOND_STAR.getEccentricity(), 0.001)))
                .andExpect(jsonPath("$.inclination", closeTo((double) SECOND_STAR.getInclination(), 0.001)))
                .andExpect(jsonPath("$.longitudeOfAscendingNode", closeTo((double) SECOND_STAR.getLongitudeOfAscendingNode(), 0.001)))
                .andExpect(jsonPath("$.argumentOfPeriapsis", closeTo((double) SECOND_STAR.getArgumentOfPeriapsis(), 0.001)))
                .andExpect(jsonPath("$.trueAnomalyAtEpoch", closeTo((double) SECOND_STAR.getTrueAnomalyAtEpoch(), 0.001)))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d",
                        FIRST_SYSTEM.getId(),
                        SECOND_STAR.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:stars", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars", FIRST_SYSTEM.getId()))));
        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetSecondaryStar_ValidSystemId_ValidStarId_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(FIRST_SYSTEM, SECOND_STAR);

        mockMvc.perform(get(String.format("/systems/%d/stars/%d", FIRST_SYSTEM.getId(), SECOND_STAR.getId())).accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchema(STAR_SCHEMA)))
                .andExpect(jsonPath("$.id", is(SECOND_STAR.getId())))
                .andExpect(jsonPath("$.name", is(SECOND_STAR.getName())))
                .andExpect(jsonPath("$.mass", closeTo(SECOND_STAR.getMass(), 0.001)))
                .andExpect(jsonPath("$.semimajorAxis", closeTo((double) SECOND_STAR.getSemimajorAxis(), 0.001)))
                .andExpect(jsonPath("$.eccentricity", closeTo((double) SECOND_STAR.getEccentricity(), 0.001)))
                .andExpect(jsonPath("$.inclination", closeTo((double) SECOND_STAR.getInclination(), 0.001)))
                .andExpect(jsonPath("$.longitudeOfAscendingNode", closeTo((double) SECOND_STAR.getLongitudeOfAscendingNode(), 0.001)))
                .andExpect(jsonPath("$.argumentOfPeriapsis", closeTo((double) SECOND_STAR.getArgumentOfPeriapsis(), 0.001)))
                .andExpect(jsonPath("$.trueAnomalyAtEpoch", closeTo((double) SECOND_STAR.getTrueAnomalyAtEpoch(), 0.001)))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars/%d",
                        FIRST_SYSTEM.getId(),
                        SECOND_STAR.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:stars", hasEntry("href", String.format(
                        "http://localhost:8080/systems/%d/stars", FIRST_SYSTEM.getId()))));

        verify(apiService, times(2)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }
}
