package com.javahero.property;

import com.javahero.model.AnswerOption;
import com.javahero.model.QuizQuestion;
import com.javahero.repository.QuizQuestionRepository;
import com.javahero.service.QuizService;
import net.jqwik.api.*;

import java.util.*;

/**
 * Property 8: Quiz sequential advancement
 *
 * For any quiz in progress, the next question SHALL only become available after
 * the current question is answered correctly. If an incorrect answer is given,
 * the quiz state SHALL remain on the same question.
 *
 * **Validates: Requirements 5.4, 5.6**
 */
class QuizAdvancementProperties {

    private static final String HERO_ID = "test-hero";
    private static final String PLAYER_ID = "test-player";

    /**
     * Creates a QuizQuestion with a given index, a correct option, and some wrong options.
     */
    private QuizQuestion createQuestion(int index) {
        QuizQuestion question = new QuizQuestion();
        question.setId("q" + index);
        question.setHeroId(HERO_ID);
        question.setQuestionText("Question " + index);

        String correctOptionId = "q" + index + "-correct";
        question.setCorrectOptionId(correctOptionId);

        List<AnswerOption> options = new ArrayList<>();

        AnswerOption correct = new AnswerOption();
        correct.setId(correctOptionId);
        correct.setText("Correct answer for question " + index);
        options.add(correct);

        for (int i = 1; i <= 2; i++) {
            AnswerOption wrong = new AnswerOption();
            wrong.setId("q" + index + "-wrong" + i);
            wrong.setText("Wrong answer " + i + " for question " + index);
            options.add(wrong);
        }

        question.setOptions(options);
        return question;
    }

    /**
     * Creates an in-memory QuizQuestionRepository stub backed by a list of questions.
     */
    private QuizQuestionRepository createMockRepository(List<QuizQuestion> questions) {
        return new InMemoryQuizQuestionRepository(questions);
    }

    /**
     * **Validates: Requirements 5.4, 5.6**
     *
     * Property: After a correct answer, the current question index advances by 1.
     * After an incorrect answer, the current question index remains unchanged.
     */
    @Property(tries = 100)
    void quizAdvancesOnlyOnCorrectAnswer(
            @ForAll("quizSizes") int numberOfQuestions,
            @ForAll("answerSequences") List<Boolean> answerSequence
    ) {
        // Create N questions for the hero
        List<QuizQuestion> questions = new ArrayList<>();
        for (int i = 0; i < numberOfQuestions; i++) {
            questions.add(createQuestion(i));
        }

        QuizQuestionRepository repository = createMockRepository(questions);
        QuizService quizService = new QuizService(repository);

        // Reset state
        quizService.resetQuizSession(PLAYER_ID, HERO_ID);

        int expectedIndex = 0;

        // Process a sequence of answers
        for (int step = 0; step < answerSequence.size() && expectedIndex < numberOfQuestions; step++) {
            boolean giveCorrectAnswer = answerSequence.get(step);
            QuizQuestion currentQuestion = questions.get(expectedIndex);

            int indexBefore = quizService.getCurrentQuestionIndex(PLAYER_ID, HERO_ID);
            assert indexBefore == expectedIndex :
                    "Before answering, expected index " + expectedIndex + " but got " + indexBefore;

            if (giveCorrectAnswer) {
                // Submit the correct answer
                boolean result = quizService.evaluateAnswer(
                        PLAYER_ID, HERO_ID,
                        currentQuestion.getId(),
                        currentQuestion.getCorrectOptionId()
                );
                assert result : "Correct answer should return true";
                expectedIndex++;
            } else {
                // Submit a wrong answer
                String wrongAnswerId = currentQuestion.getOptions().stream()
                        .filter(opt -> !opt.getId().equals(currentQuestion.getCorrectOptionId()))
                        .findFirst()
                        .map(AnswerOption::getId)
                        .orElseThrow();

                boolean result = quizService.evaluateAnswer(
                        PLAYER_ID, HERO_ID,
                        currentQuestion.getId(),
                        wrongAnswerId
                );
                assert !result : "Incorrect answer should return false";
                // Index should NOT advance
            }

            int indexAfter = quizService.getCurrentQuestionIndex(PLAYER_ID, HERO_ID);
            assert indexAfter == expectedIndex :
                    "After " + (giveCorrectAnswer ? "correct" : "incorrect") + " answer, " +
                            "expected index " + expectedIndex + " but got " + indexAfter;
        }
    }

    /**
     * **Validates: Requirements 5.4, 5.6**
     *
     * Property: Multiple incorrect answers on the same question never advance the index.
     * The state remains on the same question regardless of how many wrong attempts are made.
     */
    @Property(tries = 100)
    void multipleIncorrectAnswersDoNotAdvance(
            @ForAll("quizSizes") int numberOfQuestions,
            @ForAll("wrongAttemptCounts") int wrongAttempts
    ) {
        List<QuizQuestion> questions = new ArrayList<>();
        for (int i = 0; i < numberOfQuestions; i++) {
            questions.add(createQuestion(i));
        }

        QuizQuestionRepository repository = createMockRepository(questions);
        QuizService quizService = new QuizService(repository);

        quizService.resetQuizSession(PLAYER_ID, HERO_ID);

        QuizQuestion firstQuestion = questions.get(0);

        // Give multiple wrong answers to the first question
        for (int i = 0; i < wrongAttempts; i++) {
            String wrongAnswerId = firstQuestion.getOptions().stream()
                    .filter(opt -> !opt.getId().equals(firstQuestion.getCorrectOptionId()))
                    .findFirst()
                    .map(AnswerOption::getId)
                    .orElseThrow();

            quizService.evaluateAnswer(PLAYER_ID, HERO_ID, firstQuestion.getId(), wrongAnswerId);

            int index = quizService.getCurrentQuestionIndex(PLAYER_ID, HERO_ID);
            assert index == 0 :
                    "After " + (i + 1) + " wrong attempt(s), index should be 0 but was " + index;
        }
    }

    @Provide
    Arbitrary<Integer> quizSizes() {
        return Arbitraries.integers().between(3, 5);
    }

    @Provide
    Arbitrary<List<Boolean>> answerSequences() {
        // Generate random sequences of correct (true) / incorrect (false) answers
        return Arbitraries.of(true, false).list().ofMinSize(3).ofMaxSize(15);
    }

    @Provide
    Arbitrary<Integer> wrongAttemptCounts() {
        return Arbitraries.integers().between(1, 10);
    }

    /**
     * Simple in-memory implementation of QuizQuestionRepository for testing.
     */
    private static class InMemoryQuizQuestionRepository implements QuizQuestionRepository {

        private final List<QuizQuestion> questions;

        InMemoryQuizQuestionRepository(List<QuizQuestion> questions) {
            this.questions = new ArrayList<>(questions);
        }

        @Override
        public List<QuizQuestion> findByHeroId(String heroId) {
            return questions.stream()
                    .filter(q -> q.getHeroId().equals(heroId))
                    .toList();
        }

        @Override
        public Optional<QuizQuestion> findById(String id) {
            return questions.stream()
                    .filter(q -> q.getId().equals(id))
                    .findFirst();
        }

        // --- Unused JpaRepository methods stubbed out ---

        @Override
        public <S extends QuizQuestion> S save(S entity) { return entity; }

        @Override
        public <S extends QuizQuestion> List<S> saveAll(Iterable<S> entities) { return List.of(); }

        @Override
        public boolean existsById(String s) { return false; }

        @Override
        public List<QuizQuestion> findAll() { return questions; }

        @Override
        public List<QuizQuestion> findAllById(Iterable<String> strings) { return List.of(); }

        @Override
        public long count() { return questions.size(); }

        @Override
        public void deleteById(String s) {}

        @Override
        public void delete(QuizQuestion entity) {}

        @Override
        public void deleteAllById(Iterable<? extends String> strings) {}

        @Override
        public void deleteAll(Iterable<? extends QuizQuestion> entities) {}

        @Override
        public void deleteAll() {}

        @Override
        public void flush() {}

        @Override
        public <S extends QuizQuestion> S saveAndFlush(S entity) { return entity; }

        @Override
        public <S extends QuizQuestion> List<S> saveAllAndFlush(Iterable<S> entities) { return List.of(); }

        @Override
        public void deleteAllInBatch(Iterable<QuizQuestion> entities) {}

        @Override
        public void deleteAllByIdInBatch(Iterable<String> strings) {}

        @Override
        public void deleteAllInBatch() {}

        @Override
        @Deprecated
        public QuizQuestion getOne(String s) { return null; }

        @Override
        @Deprecated
        public QuizQuestion getById(String s) { return null; }

        @Override
        public QuizQuestion getReferenceById(String s) { return null; }

        @Override
        public <S extends QuizQuestion> Optional<S> findOne(org.springframework.data.domain.Example<S> example) { return Optional.empty(); }

        @Override
        public <S extends QuizQuestion> List<S> findAll(org.springframework.data.domain.Example<S> example) { return List.of(); }

        @Override
        public <S extends QuizQuestion> List<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Sort sort) { return List.of(); }

        @Override
        public <S extends QuizQuestion> org.springframework.data.domain.Page<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Pageable pageable) { return org.springframework.data.domain.Page.empty(); }

        @Override
        public <S extends QuizQuestion> long count(org.springframework.data.domain.Example<S> example) { return 0; }

        @Override
        public <S extends QuizQuestion> boolean exists(org.springframework.data.domain.Example<S> example) { return false; }

        @Override
        public <S extends QuizQuestion, R> R findBy(org.springframework.data.domain.Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }

        @Override
        public List<QuizQuestion> findAll(org.springframework.data.domain.Sort sort) { return questions; }

        @Override
        public org.springframework.data.domain.Page<QuizQuestion> findAll(org.springframework.data.domain.Pageable pageable) { return org.springframework.data.domain.Page.empty(); }
    }
}
