package com.careerdevs.gorestfinal.models;

import javax.persistence.*;

@Entity
public class Post {

    //Look up UUID
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)

    private Integer id;

    private Integer user_id;

    private String title;

    @Column(length = 510)
    private String body;

    public Post() {
    }

    public Post( String title, String body) {
        this.title = title;
        this.body = body;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
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
