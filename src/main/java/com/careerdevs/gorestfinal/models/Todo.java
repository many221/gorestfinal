package com.careerdevs.gorestfinal.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int user_id;
    private String title;

    private String due_on;

    private String status;

    public Todo() {
    }

    public Todo(String title, String dueDate, String status) {
        this.title = title;
        this.due_on = dueDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getDue_on() {
        return due_on;
    }

    public String getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDue_on(String due_on) {
        this.due_on = due_on;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", userId=" + user_id +
                ", title='" + title + '\'' +
                ", dueDate=" + due_on +
                ", status='" + status + '\'' +
                '}';
    }

}


