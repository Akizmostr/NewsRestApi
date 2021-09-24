package com.example.newsapi.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "creation_date")
    private LocalDate date;
    private String text;
    private String username;

    @ManyToOne
    @JoinColumn(name="news_id")
    private News news;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", date=" + date +
                ", text='" + text + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
