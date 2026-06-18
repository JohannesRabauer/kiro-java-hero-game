package com.javahero.model;

import jakarta.persistence.*;

@Entity
public class AnswerOption {

    @Id
    private String id;

    private String text;

    public AnswerOption() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
