package com.senorpez.trident.api;

import org.springframework.http.MediaType;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class SupportedMediaTypes {
    static final MediaType TRIDENT_API = new MediaType("application", "vnd.senorpez.trident.v0+json", UTF_8);
    static final String TRIDENT_API_VALUE = "application/vnd.senorpez.trident.v0+json; charset=UTF-8";

    static final MediaType FALLBACK = APPLICATION_JSON_UTF8;
    static final String FALLBACK_VALUE = APPLICATION_JSON_VALUE;
}
