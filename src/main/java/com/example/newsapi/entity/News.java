package com.example.newsapi.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue
    private Long id;

    private Date date;
    private  String text;
    private  String title;

    @OneToMany(mappedBy = "news")
    List<Comment> comments;
}
