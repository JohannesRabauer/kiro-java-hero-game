package com.javahero.service;

import com.javahero.model.Hero;
import com.javahero.model.Question;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    /**
     * Validates quiz answers. Returns true if all answers are correct.
     *
     * @param hero    the hero whose questions are being answered
     * @param answers map of question index (String) to chosen answer index (Integer)
     * @return true if every question was answered correctly
     */
    public boolean validateAnswers(Hero hero, Map<String, Integer> answers) {
        List<Question> questions = hero.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            Integer chosen = answers.get(String.valueOf(i));
            if (chosen == null) {
                return false;
            }
            if (chosen != questions.get(i).getCorrectIndex()) {
                return false;
            }
        }
        return true;
    }
}
