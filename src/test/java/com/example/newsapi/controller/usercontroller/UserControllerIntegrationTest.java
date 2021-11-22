package com.example.newsapi.controller.usercontroller;

import com.example.newsapi.NewsapiApplication;
import com.example.newsapi.dto.AddUserRolesDTO;
import com.example.newsapi.dto.UserDTO;
import com.example.newsapi.repository.NewsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static com.example.newsapi.testutils.TestUtils.badCredentials;
import static com.example.newsapi.testutils.TestUtils.conflictStatus;
import static com.example.newsapi.testutils.TestUtils.invalidEntityStatus;
import static com.example.newsapi.testutils.TestUtils.invalidPasswordMessage;
import static com.example.newsapi.testutils.TestUtils.invalidUsernameMessage;
import static com.example.newsapi.testutils.TestUtils.postJson;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = NewsapiApplication.class)
@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class UserControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    NewsRepository userRepository;

    ConstraintDescriptions userConstraints = new ConstraintDescriptions(UserDTO.class);
    ConstraintDescriptions rolesConstraints = new ConstraintDescriptions(AddUserRolesDTO.class);

    @Test
    void whenLoginWithWrongPassword_thenBadCredentials() throws Exception {
        UserDTO user = new UserDTO("user1", "wrong password");

        mockMvc.perform(postJson("/users/login", user))
                .andDo(print())
                .andExpect(badCredentials());
    }

    @Test
    void whenLoginWithWrongUser_thenBadCredentials() throws Exception {
        UserDTO user = new UserDTO("wrong user", "password");

        mockMvc.perform(postJson("/users/login", user))
                .andDo(print())
                .andExpect(badCredentials())
                .andDo(document("{class-name}/login-user-wrong",
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void whenLoginAndUsernameAndPasswordAreNotProvided_thenErrorResponse() throws Exception {
        UserDTO user = new UserDTO("", "");

        mockMvc.perform(postJson("/users/login", user))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidUsernameMessage())
                .andExpect(invalidPasswordMessage());
    }

    @Test
    void whenLoginAndUsernameIsNotProvided_thenErrorResponse() throws Exception {
        UserDTO user = new UserDTO("", "password");

        mockMvc.perform(postJson("/users/login", user))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidUsernameMessage());
    }

    @Test
    void whenLoginAndPasswordIsNotProvided_thenErrorResponse() throws Exception {
        UserDTO user = new UserDTO("user1", "");

        mockMvc.perform(postJson("/users/login", user))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(invalidPasswordMessage());
    }

    @Test
    void whenLoginAndCredentialsAreValid_thenReturnToken() throws Exception {
        UserDTO user = new UserDTO("user1", "password");

        mockMvc.perform(postJson("/users/login", user))
                .andDo(print())
                .andExpect(jsonPath("$.token", not(emptyString())))
                .andDo(document("{class-name}/login-user-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseHeaders(
                                headerWithName("Authorization").description("Access token")
                        )
                ));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenRegisterNotExistingUser_thenSuccess() throws Exception{
        UserDTO user = new UserDTO("user2", "password");

        mockMvc.perform(postJson("/users/register", user))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("User was successfully registered"))
                .andDo(document("{class-name}/register-user-success",
                        preprocessRequest(prettyPrint()),
                        requestFields(
                                attributes(key("title").value("Fields for user registration")),
                                fieldWithPath("username").description("The name of the user")
                                        .attributes(key("constraints").value(userConstraints.descriptionsForProperty("username"))),
                                fieldWithPath("password").description("The password of the user")
                                        .attributes(key("constraints").value(userConstraints.descriptionsForProperty("password")))
                        )
                ));
    }

    @Test
    void whenRegisterUserAndUserAlreadyExists_thenConflictResponse() throws Exception{
        UserDTO user = new UserDTO("user1", "password");

        mockMvc.perform(postJson("/users/register", user))
                .andDo(print())
                .andExpect(conflictStatus())
                .andExpect(jsonPath("$.error", is("User with username: user1 already exists")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenAddValidRolesAndUserFound_thenReturnCorrectNewRoles() throws Exception{
        AddUserRolesDTO addedRoles = new AddUserRolesDTO(List.of("admin", "journalist"));

        mockMvc.perform(postJson("/users/2/addRoles", addedRoles)
                .header("Authorization", "Bearer <token>"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((content().string("SUBSCRIBER, ADMIN, JOURNALIST")))
                .andDo(document("{class-name}/add-roles-success",
                        preprocessRequest(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token (see <<security, Security>>)")),
                        requestFields(
                                attributes(key("title").value("Fields for adding roles")),
                                fieldWithPath("roles").description("An array of roles to be added")
                                        .attributes(key("constraints").value("[Must not be empty], [Must have only existing roles]"))

                        )
                ));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenAddValidRolesAndUserNotFound_thenNotFoundResponse() throws Exception{
        AddUserRolesDTO addedRoles = new AddUserRolesDTO(List.of("admin", "journalist"));

        mockMvc.perform(postJson("/users/999/addRoles", addedRoles))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User with id: 999 not found")));
    }

    @Test
    void whenAddInvalidRoles_thenErrorResponse() throws Exception{
        AddUserRolesDTO addedRoles = new AddUserRolesDTO(List.of("invalid role"));

        mockMvc.perform(postJson("/users/2/addRoles", addedRoles))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(jsonPath("$.error", is("Invalid role")));
    }

    @Test
    void whenAddEmptyRolesList_thenErrorResponse() throws Exception{
        AddUserRolesDTO addedRoles = new AddUserRolesDTO(Collections.emptyList());

        mockMvc.perform(postJson("/users/2/addRoles", addedRoles))
                .andDo(print())
                .andExpect(invalidEntityStatus())
                .andExpect(jsonPath("$.error", is("Must not be empty")));
    }

    
}