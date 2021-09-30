package com.example.newsapi.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@DynamicInsert
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "creation_date")
    private LocalDate date;

    private  String text;

    @NotNull
    private  String title;

    @OneToMany(mappedBy = "news")
    List<Comment> comments;

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
