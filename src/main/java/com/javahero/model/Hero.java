package com.javahero.model;

import java.util.List;

public class Hero {

    private String id;
    private String name;
    private LearningPath learningPath;
    private int order;
    private String tagline;
    private String explanation;
    private String javaExample;
    private List<Question> questions;

    public Hero() {}

    public Hero(String id, String name, LearningPath learningPath, int order,
                String tagline, String explanation, String javaExample,
                List<Question> questions) {
        this.id = id;
        this.name = name;
        this.learningPath = learningPath;
        this.order = order;
        this.tagline = tagline;
        this.explanation = explanation;
        this.javaExample = javaExample;
        this.questions = questions;
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

    public LearningPath getLearningPath() {
        return learningPath;
    }

    public void setLearningPath(LearningPath learningPath) {
        this.learningPath = learningPath;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getJavaExample() {
        return javaExample;
    }

    public void setJavaExample(String javaExample) {
        this.javaExample = javaExample;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
