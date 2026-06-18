package com.javahero.dto;

public class AnswerRequest {

    private String questionId;
    private String answerId;

    public AnswerRequest() {
    }

    public AnswerRequest(String questionId, String answerId) {
        this.questionId = questionId;
        this.answerId = answerId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }
}
