package com.javahero.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class HeroCard {

    @Id
    private String id;

    private String name;

    private String conceptTitle;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255)")
    private LearningPath learningPath;

    private int orderInPath;

    public HeroCard() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConceptTitle() {
        return conceptTitle;
    }

    public void setConceptTitle(String conceptTitle) {
        this.conceptTitle = conceptTitle;
    }

    public LearningPath getLearningPath() {
        return learningPath;
    }

    public void setLearningPath(LearningPath learningPath) {
        this.learningPath = learningPath;
    }

    public int getOrderInPath() {
        return orderInPath;
    }

    public void setOrderInPath(int orderInPath) {
        this.orderInPath = orderInPath;
    }
}
