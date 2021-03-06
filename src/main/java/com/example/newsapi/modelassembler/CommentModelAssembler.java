package com.example.newsapi.modelassembler;

import com.example.newsapi.controller.CommentController;
import com.example.newsapi.controller.NewsController;
import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.PostCommentDTO;
import com.example.newsapi.entity.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Class that converts Comment entities to CommentDTO objects with links and vice versa
 */
@Component
public class CommentModelAssembler implements RepresentationModelAssembler<Comment, EntityModel<CommentDTO>> {
    /**
     * Converts single Comment entity to DTO object and adds corresponding links
     *
     *
     * @param comment Comment entity
     * @return Representation of comment
     */
    @Override
    public EntityModel<CommentDTO> toModel(Comment comment) {
        //Convert entity to DTO
        ModelMapper modelMapper = new ModelMapper();
        CommentDTO commentDto = modelMapper.map(comment, CommentDTO.class);

        //Add links
        EntityModel<CommentDTO> commentModel = EntityModel.of(commentDto);
        commentModel.add(linkTo(methodOn(CommentController.class).getCommentById(comment.getNews().getId(), comment.getId())).withSelfRel());
        commentModel.add(linkTo(methodOn(NewsController.class).getNewsById(comment.getNews().getId(), Pageable.unpaged())).withRel("news"));
        commentModel.add(linkTo(methodOn(CommentController.class).getAllCommentsByNews(null, comment.getNews().getId(), Pageable.unpaged())).withRel("comments"));

        return commentModel;
    }

    /**
     * Converts iterable collection of Comment entities into CollectionModel
     *
     * @param entities Iterable collection of Comment entities
     * @return CollectionModel of Comment
     */
    @Override
    public CollectionModel<EntityModel<CommentDTO>> toCollectionModel(Iterable<? extends Comment> entities) {
        List<EntityModel<CommentDTO>> comments = new ArrayList<>();

        entities.forEach((comment -> {
            comments.add(toModel(comment)); //converting each entity to dto with links and adding to the list
        }));

        return CollectionModel.of(comments);
    }

    /**
     * Converts CommentDTO object to Comment entity
     *
     * @param commentDto commentDTO object to convert
     * @return Comment entity
     */
    public Comment toEntity(CommentDTO commentDto){
        //convert dto to entity
        ModelMapper modelMapper = new ModelMapper();
        Comment comment = modelMapper.map(commentDto, Comment.class);

        return comment;
    }

    public Comment toEntity(PostCommentDTO commentDto){
        //convert dto to entity
        ModelMapper modelMapper = new ModelMapper();
        Comment comment = modelMapper.map(commentDto, Comment.class);

        return comment;
    }
}
