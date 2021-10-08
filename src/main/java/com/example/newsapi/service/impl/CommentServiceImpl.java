package com.example.newsapi.service.impl;

import com.example.newsapi.dto.CommentDTO;
import com.example.newsapi.dto.UpdateCommentDTO;
import com.example.newsapi.entity.Comment;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.CommentModelAssembler;
import com.example.newsapi.repository.CommentRepository;
import com.example.newsapi.repository.NewsRepository;
import com.example.newsapi.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

/**
 * Service class implementation for Comments
 */
@Service
public class CommentServiceImpl implements CommentService {
    private CommentRepository commentRepository;

    private NewsRepository newsRepository;

    /**
     * assembler used to convert from entity to dto with links and vice versa
     */
    private CommentModelAssembler assembler;

    /**
     * Assembler used to convert Comment entity to PagedModel.
     * pagedAssembler.toModel() accepts CommentModelAssembler as parameter
     * in order to convert entity to dto while creating PagedModel
     */
    private PagedResourcesAssembler<Comment> pagedAssembler;

    private static final Logger log = LoggerFactory.getLogger(NewsServiceImpl.class);

    public CommentServiceImpl(CommentRepository commentRepository, NewsRepository newsRepository, CommentModelAssembler assembler, PagedResourcesAssembler<Comment> pagedAssembler) {
        this.commentRepository = commentRepository;
        this.newsRepository = newsRepository;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }


    @Override
    public PagedModel<CommentDTO> getAllCommentsByNews(Specification<Comment> spec, long newsId, Pageable pageable){
        if(!newsRepository.existsById(newsId))
            throw new ResourceNotFoundException("Not found News with id " + newsId);

        /*
        Spring data jpa doesn't support using both Specification and Projection
        That's why if search specifications are present we need to manually
        Add another Specification in order to find comments by corresponding news id
        */
        if (spec != null) {
            //Specification for searching comments by news id
            Specification<Comment> specId = (Specification<Comment>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("news").get("id"), newsId);
            return pagedAssembler.toModel(
                    commentRepository.findAll(Specification.where(spec).and(specId), pageable), assembler
            );
        }
        else {
            //Suppose Jpa Projection is more efficient when Specification is not provided
            return pagedAssembler.toModel(
                    commentRepository.findAllByNewsId(newsId, pageable), assembler
            );
        }
    }

    @Override
    public CommentDTO getCommentById(long newsId, long commentId){
        if(!newsRepository.existsById(newsId))
            throw new ResourceNotFoundException("Not found News with id " + newsId);

        return commentRepository.findAllByNewsId(newsId).get() //returns all comments of the news
                .stream()
                .filter(comment -> comment.getId()==commentId) //filter comments with requested id
                .findFirst()
                .map(assembler::toModel) //map found entity to dto
                .orElseThrow(()->new ResourceNotFoundException("Not found Comment with id " + commentId));
    }

    @Override
    public CommentDTO createComment(CommentDTO commentDto, long newsId){
        return assembler.toModel(
                newsRepository.findById(newsId).map(news -> {
                    Comment comment = assembler.toEntity(commentDto); //create Comment object from dto
                    comment.setNews(news); //connect new Comment entity with found news
                    return commentRepository.save(comment); //save constructed Comment entity
                }).orElseThrow(()->new ResourceNotFoundException("Not found News with id " + newsId))
        );
    }

    @Override
    public CommentDTO updateComment(UpdateCommentDTO requestedCommentDto, long newsId, long commentId){
        if(!newsRepository.existsById(newsId))
            throw new ResourceNotFoundException("Not found News with id " + newsId);

        return assembler.toModel(
                commentRepository.findById(commentId).map(comment -> {
                    String newText = requestedCommentDto.getText();
                    //change Comment properties
                    comment.setText(newText);
                    return commentRepository.save(comment); //repository.save() also works as update
                }).orElseThrow(() -> new ResourceNotFoundException("Not found Comment with id " + commentId))
        );
    }

    @Override
    public void deleteCommentById(long newsId, long commentId){
        if(!newsRepository.existsById(newsId))
            throw new ResourceNotFoundException("Not found News with id " + newsId);

        if(!commentRepository.existsById(commentId))
            throw new ResourceNotFoundException("Not found Comment with id " + commentId);

        commentRepository.deleteById(commentId);

        log.info("Comment with id {} was successfully deleted", commentId);
        log.info("News id : {}", newsId);
    }
}
