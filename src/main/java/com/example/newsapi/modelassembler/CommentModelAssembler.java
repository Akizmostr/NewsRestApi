package com.example.newsapi.modelassembler;

import com.example.newsapi.controller.CommentController;
import com.example.newsapi.controller.NewsController;
import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CommentModelAssembler implements RepresentationModelAssembler<Comment, CommentDTO> {
    @Override
    public CommentDTO toModel(Comment comment) {
        //Convert entity to DTO
        ModelMapper modelMapper = new ModelMapper();
        CommentDTO commentDto = modelMapper.map(comment, CommentDTO.class);

        //Add links
        commentDto.add(linkTo(methodOn(CommentController.class).getCommentById(comment.getNews().getId(), comment.getId())).withSelfRel());
        commentDto.add(linkTo(methodOn(NewsController.class).getNewsById(comment.getNews().getId(), Pageable.unpaged())).withRel("news"));
        commentDto.add(linkTo(methodOn(CommentController.class).getAllCommentsByNews(null, comment.getNews().getId(), Pageable.unpaged())).withRel("comments"));

        return commentDto;
    }

    @Override
    public CollectionModel<CommentDTO> toCollectionModel(Iterable<? extends Comment> entities) {
        List<CommentDTO> comments = new ArrayList<>();

        entities.forEach((comment -> {
            comments.add(toModel(comment)); //converting each entity to dto with links and adding to the list
        }));

        return CollectionModel.of(comments);
    }

    public Comment toEntity(CommentDTO commentDto){
        //convert dto to entity
        ModelMapper modelMapper = new ModelMapper();
        Comment comment = modelMapper.map(commentDto, Comment.class);

        return comment;
    }
}
