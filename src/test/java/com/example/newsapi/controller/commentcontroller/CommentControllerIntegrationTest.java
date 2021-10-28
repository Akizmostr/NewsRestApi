package com.example.newsapi.controller.commentcontroller;

import com.example.newsapi.NewsapiApplication;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.repository.CommentRepository;
import com.example.newsapi.repository.NewsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static com.example.newsapi.testutils.TestUtils.commentNotFound;
import static com.example.newsapi.testutils.TestUtils.newsNotFound;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = NewsapiApplication.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CommentControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    NewsRepository newsRepository;

    @Autowired
    CommentRepository commentRepository;

    //getAllComments START ----------------------------------------------------

    @Test
    void whenGetAllCommentsWithoutPage_thenCorrectResponseAndDefaultPage() throws Exception {
        long id = 1;
        Page<Comment> result = commentRepository.findAllByNewsId(1, Pageable.unpaged());
        int totalElements = (int) result.getTotalElements();
        int numberOfPages = result.getTotalPages();


        mockMvc.perform(MockMvcRequestBuilders.get("/news/{id}/comments", id)
                .accept("application/hal+json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.comments", hasSize(totalElements)))
                .andExpect(jsonPath("$._links.self.href", not(emptyOrNullString())))
                .andExpect(jsonPath("$.page.totalElements", is(totalElements)))
                .andExpect(jsonPath("$.page.totalPages", is(numberOfPages)));
    }

    @Test
    void whenGetAllNewsWithPage_thenCorrectResponseAndPage() throws Exception {
        long id = 1;
        int size = 2;
        Page<Comment> result = commentRepository.findAllByNewsId(id, Pageable.ofSize(size));
        int totalElements = (int) result.getTotalElements();
        int numberOfPages = result.getTotalPages();
        int pageNumber = result.getNumber();

        mockMvc.perform(MockMvcRequestBuilders.get("/news/{id}/comments", id)
                .accept("application/hal+json")
                .param("page", "0")
                .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.comments", hasSize(2)))
                .andExpect(jsonPath("$._links.self.href", not(emptyOrNullString())))
                .andExpect(jsonPath("$.page.size", is(size)))
                .andExpect(jsonPath("$.page.totalElements", is(totalElements)))
                .andExpect(jsonPath("$.page.totalPages", is(numberOfPages)))
                .andExpect(jsonPath("$.page.number", is(pageNumber)));
    }

    @Test
    void whenSearchAllCommentsByDateAndCommentsFound_thenReturnCorrectComments() throws Exception {
        long id = 1;
        String date = "2021-01-01";
        Specification<Comment> spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("date"), LocalDate.parse(date));
        Specification<Comment> specId = (Specification<Comment>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("news").get("id"), id);
        Page<Comment> result = commentRepository.findAll(Specification.where(spec).and(specId), Pageable.unpaged());
        int totalElements = (int) result.getTotalElements();

        mockMvc.perform(MockMvcRequestBuilders.get("/news/{id}/comments", id)
                .accept("application/hal+json")
                .param("date", date))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.comments", hasSize(totalElements)))
                .andExpect(jsonPath("$.page.totalElements", is(totalElements)))
                .andExpect(jsonPath("$._embedded.comments[*].date", everyItem(is(date))));
    }

    @Test
    void whenSearchAllCommentsByDateAndCommentsNotFound_thenReturnEmptyPage() throws Exception {
        String date = "1000-01-01";

        mockMvc.perform(MockMvcRequestBuilders.get("/news/1/comments")
                .accept("application/hal+json")
                .param("date", date))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.page.totalElements", is(0)));
    }

    @Test
    void whenSearchAllCommentsByUserAndCommentsFound_thenReturnCorrectComments() throws Exception {
        long id = 1;
        String username = "user1";
        Specification<Comment> spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("username"), username);
        Specification<Comment> specId = (Specification<Comment>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("news").get("id"), id);
        Page<Comment> result = commentRepository.findAll(Specification.where(spec).and(specId), Pageable.unpaged());
        int totalElements = (int) result.getTotalElements();

        mockMvc.perform(MockMvcRequestBuilders.get("/news/{id}/comments", id)
                .accept("application/hal+json")
                .param("username", username))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.comments", hasSize(totalElements)))
                .andExpect(jsonPath("$.page.totalElements", is(totalElements)))
                .andExpect(jsonPath("$._embedded.comments[*].username", everyItem(is(username))));
    }

    @Test
    void whenSearchAllCommentsByUsernameAndCommentsNotFound_thenReturnEmptyPage() throws Exception {
        String username = "not existing user";

        mockMvc.perform(MockMvcRequestBuilders.get("/news/1/comments")
                .accept("application/hal+json")
                .param("username", username))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.page.totalElements", is(0)));
    }

    //getAllComments END ----------------------------------------------------

    //getCommentById START --------------------------------------------------

    @Test
    void whenGetCommentByIdAndCommentFound_thenCorrectComment() throws Exception {
        long newsId = 1;
        long commentId = 1;
        Comment comment = commentRepository.findAllByNewsId(newsId).get() //returns all comments of the news
                .stream()
                .filter(foundComment -> foundComment.getId()==commentId) //filter comments with requested id
                .findFirst()
                .get();
        String date = comment.getDate().toString();
        String text = comment.getText();
        String username = comment.getUsername();

        mockMvc.perform(MockMvcRequestBuilders.get("/news/{newsId}/comments/{commentId}", newsId, commentId)
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.date", is(date)))
                .andExpect(jsonPath("$.text", is(text)))
                .andExpect(jsonPath("$.username", is(username)));
    }

    @Test
    void whenGetCommentByIdAndCommentNotFound_thenCommentNotFoundResponse() throws Exception {
        long newsId = 1;
        long commentId = 999;

        mockMvc.perform(MockMvcRequestBuilders.get("/news/{newsId}/comments/{commentId}", newsId, commentId)
                .accept("application/json"))
                .andDo(print())
                .andExpect(commentNotFound(commentId));
    }

    @Test
    void whenGetCommentByIdAndNewsNotFound_thenNewsNotFoundResponse() throws Exception {
        long newsId = 999;
        long commentId = 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/news/{newsId}/comments/{commentId}", newsId, commentId)
                .accept("application/json"))
                .andDo(print())
                .andExpect(newsNotFound(newsId));
    }

    //getCommentById END -------------------------------------------------

    //deleteComment START ------------------------------------------------

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenDeleteCommentAndCommentFound_thenCorrectResponseAndCommentDoesNotExist() throws Exception {

        mockMvc.perform(delete("/news/1/comments/1")
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Assertions.assertFalse(commentRepository.existsById(1L));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenDeleteCommentAndCommentNotFound_thenCommentNotFoundResponse() throws Exception {
        long newsId = 1;
        long commentId = 999;

        mockMvc.perform(delete("/news/{newsId}/comments/{commentId}", newsId, commentId)
                .accept("application/json"))
                .andDo(print())
                .andExpect(commentNotFound(commentId));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void whenDeleteCommentAndNewsNotFound_thenNewsNotFoundResponse() throws Exception {
        long newsId = 999;
        long commentId = 1;

        mockMvc.perform(delete("/news/{newsId}/comments/{commentId}", newsId, commentId)
                .accept("application/json"))
                .andDo(print())
                .andExpect(newsNotFound(newsId));
    }

    //deleteComment END ------------------------------------------------

}