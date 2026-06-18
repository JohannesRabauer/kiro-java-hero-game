package com.javahero.dto;

public class AnswerResponse {

    private boolean correct;
    private int questionIndex;

    public AnswerResponse() {
    }

    public AnswerResponse(boolean correct, int questionIndex) {
        this.correct = correct;
        this.questionIndex = questionIndex;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }
}
