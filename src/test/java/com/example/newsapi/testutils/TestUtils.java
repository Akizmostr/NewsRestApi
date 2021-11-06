package com.example.newsapi.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
                jsonPath("$.statusCode", is(404)),
                jsonPath("$.message", is("Not found News with id " + id))
        );
    }

    public static ResultMatcher commentNotFound(long id){
        return ResultMatcher.matchAll(
                status().isNotFound(),
                jsonPath("$.statusCode", is(404)),
                jsonPath("$.message", is("Not found Comment with id " + id))
        );
    }

    public static ResultMatcher invalidEntityStatus(){
        return ResultMatcher.matchAll(
                status().isUnprocessableEntity(),
                jsonPath("$.statusCode", is(422))
        );
    }

    public static ResultMatcher conflictStatus(){
        return ResultMatcher.matchAll(
                status().isConflict(),
                jsonPath("$.statusCode", is(409))
        );
    }

    public static ResultMatcher badCredentials(){
        return ResultMatcher.matchAll(
                status().isUnauthorized(),
                jsonPath("$.statusCode", is(401)),
                jsonPath("$.message", is("Bad credentials"))
        );
    }

    public static ResultMatcher invalidTextMessage(){
        return ResultMatcher.matchAll(
                jsonPath("$.message", containsString("The text is required"))
        );
    }

    public static ResultMatcher invalidTitleMessage(){
        return ResultMatcher.matchAll(
                jsonPath("$.message", containsString("The title is required"))
        );
    }

    public static ResultMatcher invalidUsernameMessage(){
        return ResultMatcher.matchAll(
                jsonPath("$.message", containsString("The username is required"))
        );
    }

    public static ResultMatcher invalidPasswordMessage(){
        return ResultMatcher.matchAll(
                jsonPath("$.message", containsString("The password is required"))
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
        return put(url, uriVars)
                .contentType("application/json")
                .accept("application/hal+json")
                .content(json);
    }

    public static MockHttpServletRequestBuilder postJson(String url, Object body) throws JsonProcessingException {
        ObjectMapper mapper = createObjectMapper();
        String json = mapper.writeValueAsString(body);
        return post(url)
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
