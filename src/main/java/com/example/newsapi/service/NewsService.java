package com.example.newsapi.service;

import com.example.newsapi.dto.NewsDTO;
import com.example.newsapi.entity.News;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.NewsModelAssembler;
import com.example.newsapi.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

@Service
public class NewsService {
    private NewsRepository newsRepository;

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    private NewsModelAssembler assembler; //assembler used to convert from entity to dto with links and vice versa

    /*
    Assembler used to convert News entity to PagedModel.
    pagedAssembler.toModel() accepts NewsModelAssembler as parameter
    in order to convert entity to dto while creating PagedModel
    */
    @Autowired
    private PagedResourcesAssembler<News> pagedAssembler;

    public NewsService(NewsRepository newsRepository, NewsModelAssembler assembler) {
        this.newsRepository = newsRepository;
        this.assembler = assembler;
    }

    public PagedModel<NewsDTO> getAllNews(Specification<News> spec, Pageable pageable){
        return pagedAssembler.toModel(
                newsRepository.findAll(spec, pageable), assembler
        );
    }

    public NewsDTO createNews(NewsDTO news){
        return assembler.toModel(
                newsRepository.save(assembler.toEntity(news))
        );
    }

    public NewsDTO getNewsById(long id){
        return assembler.toModel(
                newsRepository.findById(id)
                        .orElseThrow(()->new ResourceNotFoundException("Not found News with id " + id))
        );
    }

    public NewsDTO updateNews(NewsDTO requestedNewsDto, long id){
        return assembler.toModel(
                newsRepository.findById(id).map(news -> {
                    //change news properties found in repository
                    news.setTitle(requestedNewsDto.getTitle());
                    news.setText(requestedNewsDto.getText());
                    return newsRepository.save(news); //repository.save() also works as update
                }).orElseThrow(()-> new ResourceNotFoundException("Not found News with id " + id))
        );
    }

    public void deleteNewsById(long id){
        if(!newsRepository.existsById(id))
            throw new ResourceNotFoundException("Not found News with id " + id);

        newsRepository.deleteById(id);
    }
}
