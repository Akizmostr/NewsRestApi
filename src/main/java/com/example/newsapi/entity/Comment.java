package com.example.newsapi.entity;

import com.sun.istack.NotNull;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
//@DynamicInsert
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "creation_date")
    private LocalDate date;

    private String text;

    @NotNull
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

    /*public long getNewsId(){
        return news.getId();
    }*/
}
