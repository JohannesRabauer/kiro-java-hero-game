package com.javahero.property;

import com.javahero.model.AnswerOption;
import com.javahero.model.QuizQuestion;
import com.javahero.repository.QuizQuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Property 7: Quiz structure validity
 *
 * For any hero card's quiz:
 * 1. The number of questions is between 3 and 5 inclusive
 * 2. Each question has between 3 and 4 answer options
 * 3. Exactly one option per question is marked as correct (correctOptionId matches one of the option IDs)
 *
 * Validates: Requirements 5.1, 5.2
 */
@SpringBootTest
class QuizStructureProperties {

    private static final List<String> ALL_HERO_IDS = List.of(
            "bronze-variable", "bronze-string", "bronze-number", "bronze-boolean",
            "bronze-if", "bronze-loop", "bronze-method",
            "silver-class", "silver-object", "silver-constructor",
            "silver-collection", "silver-exception",
            "gold-interface", "gold-generic", "gold-stream",
            "gold-lambda", "gold-optional", "gold-record",
            "spring-controller", "spring-service", "spring-repository",
            "spring-bean", "spring-di"
    );

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    /**
     * **Validates: Requirements 5.1, 5.2**
     *
     * Property: For any hero card's quiz, the number of questions is between 3 and 5 inclusive,
     * each question has between 3 and 4 answer options, and exactly one option per question
     * is marked as correct.
     */
    @Test
    @Transactional
    void quizStructureIsValidForAllHeroes() {
        for (String heroId : ALL_HERO_IDS) {
            List<QuizQuestion> questions = quizQuestionRepository.findByHeroId(heroId);

            // Property 1: Number of questions is between 3 and 5 inclusive
            assertThat(questions)
                    .as("Hero '%s' should have between 3 and 5 quiz questions", heroId)
                    .hasSizeBetween(3, 5);

            for (QuizQuestion question : questions) {
                List<AnswerOption> options = question.getOptions();

                // Property 2: Each question has between 3 and 4 answer options
                assertThat(options)
                        .as("Question '%s' for hero '%s' should have 3-4 options",
                                question.getId(), heroId)
                        .hasSizeBetween(3, 4);

                // Property 3: Exactly one option is marked as correct
                String correctOptionId = question.getCorrectOptionId();
                assertThat(correctOptionId)
                        .as("Question '%s' for hero '%s' must have a correctOptionId",
                                question.getId(), heroId)
                        .isNotNull()
                        .isNotEmpty();

                List<String> optionIds = options.stream()
                        .map(AnswerOption::getId)
                        .toList();

                assertThat(optionIds)
                        .as("correctOptionId '%s' for question '%s' (hero '%s') must match one of the option IDs %s",
                                correctOptionId, question.getId(), heroId, optionIds)
                        .contains(correctOptionId);
            }
        }
    }
}
