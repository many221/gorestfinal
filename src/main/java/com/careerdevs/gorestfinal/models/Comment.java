package com.careerdevs.gorestfinal.models;

import javax.persistence.*;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer post_id;
    private String name;
    private String email;

    @Column(length = 510)
    private String body;

    public Comment() {
    }

    public Comment(String name, String email, String body) {
        this.name = name;
        this.email = email;
        this.body = body;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPost_id() {
        return post_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBody() {
        return body;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", post_id=" + post_id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
