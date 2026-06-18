package com.javahero.service;

import com.javahero.model.QuizQuestion;
import com.javahero.repository.QuizQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuizService {

    private final QuizQuestionRepository quizQuestionRepository;

    // Tracks correctly answered question IDs per player+hero session.
    // Key: "playerId:heroId", Value: set of correctly answered question IDs
    private final Map<String, Set<String>> quizSessionState = new ConcurrentHashMap<>();

    public QuizService(QuizQuestionRepository quizQuestionRepository) {
        this.quizQuestionRepository = quizQuestionRepository;
    }

    /**
     * Returns all quiz questions for the given hero card.
     */
    public List<QuizQuestion> getQuestions(String heroId) {
        return quizQuestionRepository.findByHeroId(heroId);
    }

    /**
     * Evaluates whether the given answer is correct for the specified question.
     * If correct, tracks the answer in the session state.
     *
     * @return true if the answer is correct, false otherwise
     */
    public boolean evaluateAnswer(String playerId, String heroId, String questionId, String answerId) {
        QuizQuestion question = quizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        boolean correct = answerId.equals(question.getCorrectOptionId());

        if (correct) {
            String sessionKey = buildSessionKey(playerId, heroId);
            quizSessionState.computeIfAbsent(sessionKey, k -> new HashSet<>()).add(questionId);
        }

        return correct;
    }

    /**
     * Checks if the quiz is complete for the given player and hero.
     * A quiz is complete when all questions for that hero have been answered correctly.
     */
    public boolean isQuizComplete(String playerId, String heroId) {
        String sessionKey = buildSessionKey(playerId, heroId);
        Set<String> answeredCorrectly = quizSessionState.get(sessionKey);

        if (answeredCorrectly == null || answeredCorrectly.isEmpty()) {
            return false;
        }

        List<QuizQuestion> allQuestions = quizQuestionRepository.findByHeroId(heroId);
        return answeredCorrectly.size() >= allQuestions.size();
    }

    /**
     * Resets the quiz session state for the given player and hero.
     */
    public void resetQuizSession(String playerId, String heroId) {
        String sessionKey = buildSessionKey(playerId, heroId);
        quizSessionState.remove(sessionKey);
    }

    /**
     * Returns the number of questions already answered correctly for the given player and hero.
     * This represents the current question index for sequential advancement.
     */
    public int getCurrentQuestionIndex(String playerId, String heroId) {
        String sessionKey = buildSessionKey(playerId, heroId);
        Set<String> answeredCorrectly = quizSessionState.get(sessionKey);
        return (answeredCorrectly == null) ? 0 : answeredCorrectly.size();
    }

    private String buildSessionKey(String playerId, String heroId) {
        return playerId + ":" + heroId;
    }
}
