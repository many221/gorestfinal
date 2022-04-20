package com.careerdevs.gorestfinal.models;

import javax.persistence.*;

@Entity
public class Post {

    //Look up UUID
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private Long id;

    private Long user_id;

    private String title;

    @Column(length = 65555)
    private String body;

    public Post() {
    }

    public Post( String title, String body) {
        this.title = title;
        this.body = body;
    }

    public long getId() {
        return id;
    }

    public long getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
