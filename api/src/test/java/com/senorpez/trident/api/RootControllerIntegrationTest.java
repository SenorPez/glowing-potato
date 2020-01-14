package com.senorpez.trident.api;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static com.senorpez.trident.api.SupportedMediaTypes.FALLBACK;
import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RootControllerIntegrationTest {
    private MockMvc mockMvc;
    private TestContextManager testContextManager;

    @Autowired
    private WebApplicationContext wac;

    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "vnd.senorpez.trident.v0+json", UTF_8);
    private static final ClassLoader CLASS_LOADER = RootControllerTest.class.getClassLoader();
    private static InputStream OBJECT_SCHEMA;
    private static InputStream ERROR_SCHEMA;

    @Before
    public void setUp() throws Exception {
        OBJECT_SCHEMA = CLASS_LOADER.getResourceAsStream("root.schema.json");
        ERROR_SCHEMA = CLASS_LOADER.getResourceAsStream("error.schema.json");

        this.testContextManager = new TestContextManager(getClass());
        this.testContextManager.prepareTestInstance(this);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    public void getRoot_ValidAcceptHeader() throws Exception {
        mockMvc.perform(get("/").accept(TRIDENT_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TRIDENT_API))
                .andExpect(content().string(matchesJsonSchema(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.trident-api:systems", hasEntry("href", "http://localhost/systems")))
                .andExpect(jsonPath("$._links.trident-api:constants", hasEntry("href", "http://localhost/constants")));
    }

    @Test
    public void getRoot_FallbackAcceptHeader() throws Exception {
        mockMvc.perform(get("/").accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchema(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.trident-api:systems", hasEntry("href", "http://localhost/systems")))
                .andExpect(jsonPath("$._links.trident-api:constants", hasEntry("href", "http://localhost/constants")));
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
