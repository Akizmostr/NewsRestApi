package com.example.newsapi.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUtils {
    public static ResultMatcher newsNotFound(long id){
        return ResultMatcher.matchAll(
                status().isNotFound(),
                jsonPath("$.status", is(404)),
                jsonPath("$.error", is("Not found News with id " + id))
        );
    }

    public static ResultMatcher commentNotFound(long id){
        return ResultMatcher.matchAll(
                status().isNotFound(),
                jsonPath("$.status", is(404)),
                jsonPath("$.error", is("Not found Comment with id " + id))
        );
    }

    public static ResultMatcher invalidEntityStatus(){
        return ResultMatcher.matchAll(
                status().isUnprocessableEntity(),
                jsonPath("$.status", is(422))
        );
    }

    public static ResultMatcher conflictStatus(){
        return ResultMatcher.matchAll(
                status().isConflict(),
                jsonPath("$.status", is(409))
        );
    }

    public static ResultMatcher badCredentials(){
        return ResultMatcher.matchAll(
                status().isUnauthorized(),
                jsonPath("$.status", is(401)),
                jsonPath("$.error", is("Bad credentials"))
        );
    }

    public static ResultMatcher invalidTextMessage(){
        return ResultMatcher.matchAll(
                jsonPath("$.error", containsString("The text is required"))
        );
    }

    public static ResultMatcher invalidTitleMessage(){
        return ResultMatcher.matchAll(
                jsonPath("$.error", containsString("The title is required"))
        );
    }

    public static ResultMatcher invalidUsernameMessage(){
        return ResultMatcher.matchAll(
                jsonPath("$.error", containsString("The username is required"))
        );
    }

    public static ResultMatcher invalidPasswordMessage(){
        return ResultMatcher.matchAll(
                jsonPath("$.error", containsString("The password is required"))
        );
    }

    public static ResultMatcher invalidTextAndTitleMessage(){
        return ResultMatcher.matchAll(
                invalidTextMessage(),
                invalidTitleMessage()
        );
    }

    public static ResultMatcher invalidTextAndUsernameMessage(){
        return ResultMatcher.matchAll(
                invalidTextMessage(),
                invalidUsernameMessage()
        );
    }

    public static MockHttpServletRequestBuilder putJson(String url, Object body, Object... uriVars) throws JsonProcessingException {
        ObjectMapper mapper = createObjectMapper();
        String json = mapper.writeValueAsString(body);
        return RestDocumentationRequestBuilders.put(url, uriVars)
                .contentType("application/json")
                .accept("application/hal+json")
                .content(json);
    }

    public static MockHttpServletRequestBuilder postJson(String url, Object body, Object... uriVars) throws JsonProcessingException {
        ObjectMapper mapper = createObjectMapper();
        String json = mapper.writeValueAsString(body);
        return RestDocumentationRequestBuilders.post(url, uriVars)
                .contentType("application/json")
                .accept("application/hal+json")
                .content(json);
    }

    private static ObjectMapper createObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
