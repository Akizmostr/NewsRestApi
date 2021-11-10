package com.example.newsapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@DynamicInsert
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "creation_date")
    private LocalDate date;

    private  String text;

    @NotNull
    private  String title;

    @OneToMany(mappedBy = "news")
    List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", date=" + date +
                ", text='" + text + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
