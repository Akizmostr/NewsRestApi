package com.example.newsapi.validation;

import com.example.newsapi.dto.UpdateNewsDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UpdateNewsValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdateNewsDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UpdateNewsDTO news = (UpdateNewsDTO) target;

        String newTitle = news.getTitle();
        String newText = news.getText();

        if(newTitle == null && newText == null)
            errors.reject("1", "At least one field is required");

        if(newTitle != null)
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "2", "The title is required");
        if(newText != null)
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "text", "3", "The text is required");
    }
}
