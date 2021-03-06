package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RestControllerAdvice
class APIExceptionHandler {
    @ExceptionHandler({
            SolarSystemNotFoundException.class,
            StarNotFoundException.class,
            PlanetNotFoundException.class,
            PlanetaryCalendarNotFoundException.class
    })
    ResponseEntity<ErrorResponse> handleAPIObjectNotFound(final Exception e) {
        return ResponseEntity
                .status(NOT_FOUND)
                .contentType(APPLICATION_JSON_UTF8)
                .body(new ErrorResponse(NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErrorResponse> handle405MethodNotAllowed() {
        return ResponseEntity
                .status(METHOD_NOT_ALLOWED)
                .contentType(APPLICATION_JSON_UTF8)
                .body(new ErrorResponse(METHOD_NOT_ALLOWED, "Only GET methods allowed."));
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    ResponseEntity<ErrorResponse> handle406NotAcceptable() {
        return ResponseEntity
                .status(NOT_ACCEPTABLE)
                .contentType(APPLICATION_JSON_UTF8)
                .body(new ErrorResponse(NOT_ACCEPTABLE,
                        String.format("Accept header must be \"%s\"", TRIDENT_API.toString())));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handle500ServerError(Exception ex, HttpServletRequest request) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .contentType(APPLICATION_JSON_UTF8)
                .body(new ErrorResponse(INTERNAL_SERVER_ERROR,
                        String.format(">>>>>Internal server error occurred.<<<<<\r\n " +
                                ">>>>>Please report to https://github.com/SenorPez/glowing-potato/issues<<<< \r\n " +
                                "%s \r\n %s", stringWriter.toString(), request.getRequestURI())));
    }

    private class ErrorResponse {
        @JsonProperty("code")
        private final int code;
        @JsonProperty("message")
        private final String message;
        @JsonProperty("detail")
        private final String detail;

        private ErrorResponse(final HttpStatus httpStatus, final String detail) {
            this.code = httpStatus.value();
            this.message = httpStatus.getReasonPhrase();
            this.detail = detail;
        }
    }
}
