package com.javahero.dto;

import com.javahero.model.AnswerOption;
import com.javahero.model.QuizQuestion;

import java.util.List;

/**
 * DTO for quiz questions that excludes the correctOptionId to prevent cheating.
 */
public class QuizQuestionResponse {

    private String id;
    private String heroId;
    private String questionText;
    private List<AnswerOption> options;

    public QuizQuestionResponse() {
    }

    public static QuizQuestionResponse fromEntity(QuizQuestion question) {
        QuizQuestionResponse dto = new QuizQuestionResponse();
        dto.setId(question.getId());
        dto.setHeroId(question.getHeroId());
        dto.setQuestionText(question.getQuestionText());
        dto.setOptions(question.getOptions());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeroId() {
        return heroId;
    }

    public void setHeroId(String heroId) {
        this.heroId = heroId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<AnswerOption> getOptions() {
        return options;
    }

    public void setOptions(List<AnswerOption> options) {
        this.options = options;
    }
}
