package com.example.newsapi.testutils;

import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
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

    public static ResultMatcher invalidTextAndTitleMessage(){
        return ResultMatcher.matchAll(
                jsonPath("$.message", containsString("The text is required")),
                jsonPath("$.message", containsString("The title is required"))
        );
    }
}
