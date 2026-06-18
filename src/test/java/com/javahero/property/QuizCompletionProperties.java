package com.javahero.property;

import com.javahero.model.AnswerOption;
import com.javahero.model.QuizQuestion;
import com.javahero.repository.QuizQuestionRepository;
import com.javahero.service.QuizService;
import net.jqwik.api.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

/**
 * Property 9: Quiz completion logic
 *
 * For any quiz where every question has been answered correctly, the quiz state
 * is marked as "passed" (isQuizComplete returns true). Conversely, if any question
 * has not been answered correctly, the quiz is NOT complete.
 *
 * **Validates: Requirements 5.5**
 */
class QuizCompletionProperties {

    private static final String PLAYER_ID = "player-1";
    private static final String HERO_ID = "hero-1";

    /**
     * Creates a list of QuizQuestion objects with the given number of questions.
     * Each question has a unique ID and a correct option ID.
     */
    private List<QuizQuestion> createQuestions(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> {
                    QuizQuestion q = new QuizQuestion();
                    q.setId("q" + i);
                    q.setHeroId(HERO_ID);
                    q.setQuestionText("Question " + i);
                    q.setCorrectOptionId("q" + i + "-correct");

                    AnswerOption correct = new AnswerOption();
                    correct.setId("q" + i + "-correct");
                    correct.setText("Correct answer");

                    AnswerOption wrong = new AnswerOption();
                    wrong.setId("q" + i + "-wrong");
                    wrong.setText("Wrong answer");

                    q.setOptions(List.of(correct, wrong));
                    return q;
                })
                .collect(Collectors.toList());
    }

    /**
     * Creates a QuizService with a mocked repository that returns the given questions.
     */
    private QuizService createServiceWithQuestions(List<QuizQuestion> questions) {
        QuizQuestionRepository mockRepo = mock(QuizQuestionRepository.class);
        when(mockRepo.findByHeroId(HERO_ID)).thenReturn(questions);

        for (QuizQuestion q : questions) {
            when(mockRepo.findById(q.getId())).thenReturn(Optional.of(q));
        }

        return new QuizService(mockRepo);
    }

    /**
     * **Validates: Requirements 5.5**
     *
     * Property: When ALL questions have been answered correctly, the quiz is complete (passed).
     */
    @Property(tries = 100)
    void quizIsCompleteWhenAllQuestionsAnsweredCorrectly(
            @ForAll("questionCounts") int questionCount
    ) {
        List<QuizQuestion> questions = createQuestions(questionCount);
        QuizService quizService = createServiceWithQuestions(questions);

        // Answer all questions correctly
        for (QuizQuestion q : questions) {
            boolean result = quizService.evaluateAnswer(PLAYER_ID, HERO_ID, q.getId(), q.getCorrectOptionId());
            assert result : "Correct answer should be evaluated as true for question " + q.getId();
        }

        // Quiz should be complete
        boolean isComplete = quizService.isQuizComplete(PLAYER_ID, HERO_ID);
        assert isComplete : "Quiz should be complete when all " + questionCount + " questions are answered correctly";
    }

    /**
     * **Validates: Requirements 5.5**
     *
     * Property: When a proper subset of questions has been answered correctly (not all),
     * the quiz is NOT complete.
     */
    @Property(tries = 100)
    void quizIsNotCompleteWhenOnlySubsetAnsweredCorrectly(
            @ForAll("questionCounts") int questionCount,
            @ForAll("subsetProvider") Set<Integer> answeredIndices
    ) {
        // Ensure we have a proper subset (not all questions answered)
        List<QuizQuestion> questions = createQuestions(questionCount);

        // Filter indices to be within range and ensure it's a proper subset
        Set<Integer> validIndices = answeredIndices.stream()
                .filter(i -> i >= 0 && i < questionCount)
                .collect(Collectors.toSet());

        // Skip if all questions would be answered (not a proper subset)
        Assume.that(validIndices.size() < questionCount);

        QuizService quizService = createServiceWithQuestions(questions);

        // Answer only the subset of questions correctly
        for (int idx : validIndices) {
            QuizQuestion q = questions.get(idx);
            quizService.evaluateAnswer(PLAYER_ID, HERO_ID, q.getId(), q.getCorrectOptionId());
        }

        // Quiz should NOT be complete
        boolean isComplete = quizService.isQuizComplete(PLAYER_ID, HERO_ID);
        assert !isComplete : "Quiz should NOT be complete when only " + validIndices.size()
                + " of " + questionCount + " questions are answered correctly";
    }

    /**
     * **Validates: Requirements 5.5**
     *
     * Property: When no questions have been answered, the quiz is NOT complete.
     */
    @Property(tries = 100)
    void quizIsNotCompleteWhenNoQuestionsAnswered(
            @ForAll("questionCounts") int questionCount
    ) {
        List<QuizQuestion> questions = createQuestions(questionCount);
        QuizService quizService = createServiceWithQuestions(questions);

        // Don't answer any questions
        boolean isComplete = quizService.isQuizComplete(PLAYER_ID, HERO_ID);
        assert !isComplete : "Quiz should NOT be complete when no questions have been answered";
    }

    /**
     * Provides question counts between 3 and 5 (valid quiz sizes per requirements).
     */
    @Provide
    Arbitrary<Integer> questionCounts() {
        return Arbitraries.integers().between(3, 5);
    }

    /**
     * Provides random subsets of indices (0-based) representing which questions are answered.
     */
    @Provide
    Arbitrary<Set<Integer>> subsetProvider() {
        return Arbitraries.integers().between(0, 4)
                .set()
                .ofMinSize(0)
                .ofMaxSize(4);
    }
}
