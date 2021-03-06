package com.example.newsapi.service.impl;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.PostCommentDTO;
import com.example.newsapi.dto.UpdateCommentDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import com.example.newsapi.entity.Role;
import com.example.newsapi.entity.User;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.CommentModelAssembler;
import com.example.newsapi.repository.CommentRepository;
import com.example.newsapi.repository.NewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @InjectMocks
    CommentServiceImpl commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    NewsRepository newsRepository;

    @Mock
    CommentModelAssembler assembler;

    @Mock
    PagedResourcesAssembler<Comment> pagedAssembler;

    @Mock
    Clock clock;

    User user1;
    LocalDate date = LocalDate.of(2021, 9, 9);
    Clock fixedClock;
    News news1;
    News news2;
    Comment comment1;
    Comment comment2;
    CommentDTO commentDto1;
    CommentDTO commentDto2;
    List<Comment> comments;
    List<CommentDTO> commentsDto;
    List<EntityModel<CommentDTO>> commentsModel;
    Page<Comment> commentsPage;
    PagedModel<EntityModel<CommentDTO>> commentsDtoPagedModel;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        Set<Role> roles1 = new HashSet<>(Collections.singleton(new Role(1, "JOURNALIST")));
        user1 = new User(1, "user1", "password", roles1, null);

        news1 = new News(1, date, "test text 1", "test title 1", null, user1);
        news2 = new News(2, date, "test text 2", "test title 2", null, user1);

        comment1 = new Comment(1, date,"text 1", "user 1", news1);
        comment2 = new Comment(2, date,"text 2", "user 2", news1);

        commentDto1 = new CommentDTO(date,"text 1", "user 1");
        commentDto2 = new CommentDTO(date,"text 2", "user 2");

        comments = List.of(comment1, comment2);
        commentsDto = List.of(commentDto1, commentDto2);

        commentsModel = commentsDto
                .stream()
                .map(commentDto -> EntityModel.of(commentDto))
                .toList();

        news1.setComments(comments);

        commentsPage = new PageImpl<>(comments);
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(2, 0, 2);
        commentsDtoPagedModel = PagedModel.of(commentsModel, pageMetadata);

        fixedClock = Clock.fixed(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    }

    //getCommentById

    @Test
    void whenFindCommentByIdAndNewsNotFound_thenNewsNotFoundException(){
        when(newsRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentByNews(1, 1));

    }

    @Test
    void whenFindCommentByIdAndCommentNotFound_thenCommentNotFoundException(){
        when(newsRepository.existsById(anyLong())).thenReturn(true);
        when(commentRepository.findAllByNewsId(anyLong())).thenReturn(Optional.of(Collections.emptyList()));

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentByNews(1, 1));

    }

    //createComment

    @Test
    void whenCreateCommentAndNewsNotFound_thenNewsNotFoundException(){
        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(any(PostCommentDTO.class), 1));
    }

    @Test
    void createComment() {
        PostCommentDTO requestedCommentDto = new PostCommentDTO("text 3", "user 1");
        Comment comment = new Comment(3, date,"text 3", "user 1", null);
        CommentDTO commentDto = new CommentDTO(date, "text 3", "user 1");

        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
        when(newsRepository.findById(anyLong())).thenReturn(Optional.of(news2));
        when(assembler.toEntity(requestedCommentDto)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(assembler.toModel(any(Comment.class))).thenReturn(EntityModel.of(commentDto));

        CommentDTO result = commentService.createComment(requestedCommentDto, 2).getContent();

        assertNotNull(result);
        assertEquals(news2, comment.getNews());
        assertEquals(result.getDate(), date);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    //updateComment

    @Test
    void whenUpdateCommentAndNewsNotFound_thenNewsNotFoundException(){
        when(newsRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(any(UpdateCommentDTO.class), 1, 1));
    }

    @Test
    void whenUpdateCommentAndCommentNotFound_thenCommentNotFoundException(){
        when(newsRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(any(UpdateCommentDTO.class), 1, 1));
    }

    @Test
    void updateComment() {
        long newsId = 1;
        long commentId = 1;
        UpdateCommentDTO requestedCommentDto = new UpdateCommentDTO();
        requestedCommentDto.setText("new text");

        CommentDTO updatedCommentDto = new CommentDTO(date,"new text", "user 1");

        when(newsRepository.existsById(anyLong())).thenReturn(true);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment1));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment1);
        when(assembler.toModel(any(Comment.class))).thenReturn(EntityModel.of(updatedCommentDto));

        CommentDTO result = commentService.updateComment(requestedCommentDto, newsId, commentId).getContent();

        assertNotNull(result);
        assertEquals("new text", comment1.getText());
        verify(commentRepository, times(1)).save(any(Comment.class));

    }


    //getAllCommentsByNews

    @Test
    void whenGetAllCommentsByNewsAndNewsNotFound_thenNewsNotFoundException(){
        assertThrows(ResourceNotFoundException.class, () -> commentService.getAllCommentsByNews(null, 1, Pageable.unpaged()));
    }


    @Test
    void whenGetAllCommentsByNewsAndSpecificationIsNull_thenFindAllByNewsIdIsInvoked(){
        when(newsRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findAllByNewsId(1, Pageable.unpaged())).thenReturn(commentsPage);
        when(pagedAssembler.toModel(any(Page.class), any(CommentModelAssembler.class))).thenReturn(commentsDtoPagedModel);

        PagedModel<EntityModel<CommentDTO>> result = commentService.getAllCommentsByNews(null, 1, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(2, result.getMetadata().getTotalElements());
        verify(commentRepository, times(1)).findAllByNewsId(anyLong(), any(Pageable.class));
        verify(commentRepository, times(0)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void whenGetAllCommentsByNewsAndSpecificationIsNotNull_thenFindAllWithSpecificationIsInvoked(){
        long newsId = 1;

        // ???????
        // how to test if specification is correct
        Specification<Comment> specId = (Specification<Comment>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("news").get("id"), newsId);

        when(newsRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(commentsPage);
        when(pagedAssembler.toModel(any(Page.class), any(CommentModelAssembler.class))).thenReturn(commentsDtoPagedModel);

        PagedModel<EntityModel<CommentDTO>> result = commentService.getAllCommentsByNews(specId, 1, Pageable.unpaged());

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(2, result.getMetadata().getTotalElements());
        verify(commentRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(commentRepository, times(0)).findAllByNewsId(anyLong(), any(Pageable.class));
    }

    //deleteCommentById

    @Test
    void whenDeleteCommentAndNewsNotFound_thenNewsNotFoundException(){
        when(newsRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteCommentById(1, 1));
    }

    @Test
    void whenDeleteCommentAndCommentNotFound_thenCommentNotFoundException(){
        when(newsRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteCommentById(1, 1));
    }

    @Test
    void deleteCommentById() {
        long newsId = 1;
        long commentId = 1;

        when(newsRepository.existsById(anyLong())).thenReturn(true);
        when(commentRepository.existsById(anyLong())).thenReturn(true);

        commentService.deleteCommentById(newsId, commentId);

        verify(commentRepository, times(1)).deleteById(anyLong());

    }


}

