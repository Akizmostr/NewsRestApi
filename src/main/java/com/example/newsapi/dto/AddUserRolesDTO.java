package com.example.newsapi.dto;

import com.example.newsapi.config.security.RoleConstants;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.StringJoiner;

import static com.example.newsapi.config.security.RoleConstants.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AddUserRolesDTO {

    @NotNull(message = "Must not be null")
    List<@Pattern(regexp = "admin|subscriber|journalist", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Some roles don't exist") String> roles;
}
