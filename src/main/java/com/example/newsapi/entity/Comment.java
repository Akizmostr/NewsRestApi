package com.example.newsapi.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    private Date date;
    private String text;
    private String username;

    @ManyToOne
    @JoinColumn(name="news_id")
    private News news;
}
