package com.example.newsapi.validation;

import com.example.newsapi.dto.UpdateNewsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.*;

class UpdateNewsValidatorTest {

    UpdateNewsValidator validator = new UpdateNewsValidator();

    @Test
    void supports() {
        assertTrue(validator.supports(UpdateNewsDTO.class));
    }

    @Test
    void whenAllFieldsAreNull_thenReject() {
        UpdateNewsDTO requested = new UpdateNewsDTO(null, null);

        Errors errors = new BindException(requested, "requested");

        validator.validate(requested, errors);

        assertTrue(errors.hasErrors());
        assertEquals(errors.getGlobalError().getCode(), "1");
        assertEquals(errors.getGlobalError().getDefaultMessage(), "At least one field is required");
    }

    @Test
    void whenTitleIsInvalid_thenRejectTitleAndAcceptObject() {
        UpdateNewsDTO requested = new UpdateNewsDTO("text", " ");

        Errors errors = new BindException(requested, "requested");

        validator.validate(requested, errors);

        assertTrue(errors.hasFieldErrors("title"));
        assertEquals(errors.getFieldError("title").getCode(), "2");
        assertEquals(errors.getFieldError("title").getDefaultMessage(), "Title could not be empty");

        assertFalse(errors.hasGlobalErrors());
        assertFalse(errors.hasFieldErrors("text"));
    }

    @Test
    void whenTextIsInvalid_thenRejectTitleAndAcceptObject() {
        UpdateNewsDTO requested = new UpdateNewsDTO(" ", "title");

        Errors errors = new BindException(requested, "requested");

        validator.validate(requested, errors);

        assertTrue(errors.hasFieldErrors("text"));
        assertEquals(errors.getFieldError("text").getCode(), "3");
        assertEquals(errors.getFieldError("text").getDefaultMessage(), "Text could not be empty");

        assertFalse(errors.hasGlobalErrors());
        assertFalse(errors.hasFieldErrors("title"));
    }

    @Test
    void whenAllFieldsAreInvalid_thenRejectFieldsAndAcceptObject() {
        UpdateNewsDTO requested = new UpdateNewsDTO(" ", "");

        Errors errors = new BindException(requested, "requested");

        validator.validate(requested, errors);

        assertTrue(errors.hasFieldErrors("title"));
        assertEquals(errors.getFieldError("title").getCode(), "2");
        assertEquals(errors.getFieldError("title").getDefaultMessage(), "Title could not be empty");

        assertTrue(errors.hasFieldErrors("text"));
        assertEquals(errors.getFieldError("text").getCode(), "3");
        assertEquals(errors.getFieldError("text").getDefaultMessage(), "Text could not be empty");

        assertFalse(errors.hasGlobalErrors());
    }
}
