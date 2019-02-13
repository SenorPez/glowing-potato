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
import java.util.Collections;
import java.util.EnumSet;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static com.senorpez.trident.api.Constant.G;
import static com.senorpez.trident.api.DocumentationCommon.commonLinks;
import static com.senorpez.trident.api.SupportedMediaTypes.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
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

public class ConstantControllerTest {
    private MockMvc mockMvc;
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "invalid+json", UTF_8);
    private static final ClassLoader CLASS_LOADER = ConstantControllerTest.class.getClassLoader();
    private static InputStream CONSTANT_SCHEMA;
    private static InputStream CONSTANT_COLLECTION_SCHEMA;
    private static InputStream ERROR_SCHEMA;

    @InjectMocks
    ConstantController constantController;

    @Mock
    private APIService apiService;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Before
    public void setUp() {
        CONSTANT_SCHEMA = CLASS_LOADER.getResourceAsStream("constant.schema.json");
        CONSTANT_COLLECTION_SCHEMA = CLASS_LOADER.getResourceAsStream("constants.schema.json");
        ERROR_SCHEMA = CLASS_LOADER.getResourceAsStream("error.schema.json");
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new ConstantController(apiService))
                .setMessageConverters(HALMessageConverter.getConverter(Collections.singletonList(ALL)))
                .setControllerAdvice(new APIExceptionHandler())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void GetAllConstants_ValidAcceptHeader() throws Exception {
        when(apiService.findAll(any())).thenReturn(Collections.singleton(EnumSet.allOf(Constant.class)));

        mockMvc.perform(get("/constants").accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(CONSTANT_COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.trident-api:constant", hasItem(
                        allOf(
                                hasEntry("symbol", (Object) "G"),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", "http://localhost:8080/constants/G")))))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost:8080/constants")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andDo(document("constants",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(TRIDENT_API_VALUE))),
                        responseFields(
                                fieldWithPath("_embedded.trident-api:constant").description("Constant resource."),
                                fieldWithPath("_embedded.trident-api:constant[].symbol").description("Constant symbol."),
                                subsectionWithPath("_links").ignored(),
                                subsectionWithPath("_embedded.trident-api:constant[]._links").ignored()),
                        commonLinks));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllConstants_FallbackAcceptHeader() throws Exception {
        when(apiService.findAll(any())).thenReturn(Collections.singleton(EnumSet.allOf(Constant.class)));

        mockMvc.perform(get("/constants").accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchema(CONSTANT_COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.trident-api:constant", hasItem(
                        allOf(
                                hasEntry("symbol", (Object) "G"),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", "http://localhost:8080/constants/G")))))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost:8080/constants")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllConstants_InvalidAcceptHeader() throws Exception {
        when(apiService.findAll(any())).thenReturn(Collections.singleton(EnumSet.allOf(Constant.class)));

        mockMvc.perform(get("/constants").accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"vnd.senorpez.trident.v0+json")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetAllConstants_InvalidMethod() throws Exception {
        when(apiService.findAll(any())).thenReturn(Collections.singleton(EnumSet.allOf(Constant.class)));

        mockMvc.perform(put("/constants").accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetConstant_G_ValidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(G);

        mockMvc.perform(get(String.format("/constants/%s", G.getSymbol())).accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(CONSTANT_SCHEMA)))
                .andExpect(jsonPath("$.name", is("Newtonian constant of gravitation")))
                .andExpect(jsonPath("$.symbol", is("G")))
                .andExpect(jsonPath("$.value", closeTo(6.67408e-11, 0.00001e-11)))
                .andExpect(jsonPath("$.units", is("m^3*kg^-1*s^-2")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/constants/%s", G.getSymbol()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:constants", hasEntry("href", "http://localhost:8080/constants")))
                .andDo(document("constant",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(TRIDENT_API_VALUE))),
                        responseFields(
                                fieldWithPath("name").description("Constant name."),
                                fieldWithPath("symbol").description("Constant symbol."),
                                fieldWithPath("value").description("Constant value."),
                                fieldWithPath("units").description("Constant units."),
                                subsectionWithPath("_links").ignored()),
                        commonLinks.and(
                                linkWithRel("trident-api:constants").description("List of constants resources."))));

        verify(apiService, times(1)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetConstant_G_FallbackAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(G);

        mockMvc.perform(get(String.format("/constants/%s", G.getSymbol())).accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchema(CONSTANT_SCHEMA)))
                .andExpect(jsonPath("$.name", is("Newtonian constant of gravitation")))
                .andExpect(jsonPath("$.symbol", is("G")))
                .andExpect(jsonPath("$.value", closeTo(6.67408e-11, 0.00001e-11)))
                .andExpect(jsonPath("$.units", is("m^3*kg^-1*s^-2")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080/")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format(
                        "http://localhost:8080/constants/%s", G.getSymbol()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-trident-{rel}"),
                                hasEntry("name", (Object) "trident-api"),
                                hasEntry("templated", (Object) true)))))
                .andExpect(jsonPath("$._links.trident-api:constants", hasEntry("href", "http://localhost:8080/constants")));

        verify(apiService, times(1)).findOne(any(), any(), any());
        verifyNoMoreInteractions(apiService);
    }

    @Test
    public void GetConstant_G_InvalidAcceptHeader() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(G);

        mockMvc.perform(get(String.format("/constants/%s", G.getSymbol())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"vnd.senorpez.trident.v0+json")));

        verifyZeroInteractions(apiService);
    }

    @Test
    public void GetConstant_G_InvalidMethod() throws Exception {
        when(apiService.findOne(any(), any(), any())).thenReturn(G);

        mockMvc.perform(put(String.format("/constants/%s", G.getSymbol())).accept(TRIDENT_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Only GET methods allowed.")));

        verifyZeroInteractions(apiService);
    }
}
