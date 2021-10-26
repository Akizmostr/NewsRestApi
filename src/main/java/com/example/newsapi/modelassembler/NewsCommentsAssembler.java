package com.example.newsapi.modelassembler;

import com.example.newsapi.controller.CommentController;
import com.example.newsapi.controller.NewsController;
import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.NewsCommentsDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.entity.News;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Class that converts News entities to NewsCommentsDTO objects with links and vice versa
 */
@Component
public class NewsCommentsAssembler implements RepresentationModelAssembler<News, EntityModel<NewsCommentsDTO>> {

    /**
     * Converts single News entity to NewsCommentDTO object and adds corresponding links
     *
     * @param entity News entity
     * @return Representation of NewsComment
     */
    @Override
    public EntityModel<NewsCommentsDTO> toModel(News entity) {
        ModelMapper modelMapper = new ModelMapper();
        NewsCommentsDTO newsCommentsDto = modelMapper.map(entity, NewsCommentsDTO.class);

        List<Comment> comments = entity.getComments();

        List<CommentDTO> commentsDto = modelMapper.map(comments, new TypeToken<List<CommentDTO>>(){}.getType());

        newsCommentsDto.setComments(commentsDto);

        //add links
        EntityModel<NewsCommentsDTO> newsCommentsModel = EntityModel.of(newsCommentsDto);
        newsCommentsModel.add(linkTo(methodOn(NewsController.class).getNewsById(entity.getId(), Pageable.unpaged())).withSelfRel());
        newsCommentsModel.add(linkTo(methodOn(CommentController.class).getAllCommentsByNews(null, entity.getId(), Pageable.unpaged())).withRel("comments"));
        newsCommentsModel.add(linkTo(methodOn(NewsController.class).getAllNews(null, Pageable.unpaged())).withRel("news"));

        return newsCommentsModel;
    }
}
