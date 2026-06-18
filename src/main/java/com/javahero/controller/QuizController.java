package com.javahero.controller;

import com.javahero.dto.AnswerRequest;
import com.javahero.dto.AnswerResponse;
import com.javahero.dto.PlayerState;
import com.javahero.dto.QuizQuestionResponse;
import com.javahero.model.Player;
import com.javahero.model.QuizQuestion;
import com.javahero.service.PlayerService;
import com.javahero.service.ProgressionService;
import com.javahero.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private static final String DEFAULT_PLAYER_USERNAME = "default-player";

    private final QuizService quizService;
    private final ProgressionService progressionService;
    private final PlayerService playerService;

    public QuizController(QuizService quizService,
                          ProgressionService progressionService,
                          PlayerService playerService) {
        this.quizService = quizService;
        this.progressionService = progressionService;
        this.playerService = playerService;
    }

    /**
     * GET /api/quiz/{heroId} — Returns quiz questions for a hero card.
     * The correctOptionId is stripped from the response to prevent cheating.
     */
    @GetMapping("/{heroId}")
    public ResponseEntity<List<QuizQuestionResponse>> getQuestions(@PathVariable String heroId) {
        List<QuizQuestion> questions = quizService.getQuestions(heroId);
        List<QuizQuestionResponse> response = questions.stream()
                .map(QuizQuestionResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/quiz/{heroId}/answer — Evaluates a single answer.
     * Returns whether the answer is correct and the current question index.
     */
    @PostMapping("/{heroId}/answer")
    public ResponseEntity<AnswerResponse> evaluateAnswer(
            @PathVariable String heroId,
            @RequestBody AnswerRequest request) {
        Player player = playerService.restoreOrCreatePlayer(DEFAULT_PLAYER_USERNAME);
        String playerId = player.getId().toString();

        boolean correct = quizService.evaluateAnswer(
                playerId, heroId, request.getQuestionId(), request.getAnswerId());

        int questionIndex = quizService.getCurrentQuestionIndex(playerId, heroId);

        return ResponseEntity.ok(new AnswerResponse(correct, questionIndex));
    }

    /**
     * POST /api/quiz/{heroId}/complete — Marks quiz as passed and triggers card unlock.
     * Returns 400 Bad Request if the quiz is not yet complete.
     */
    @PostMapping("/{heroId}/complete")
    public ResponseEntity<PlayerState> completeQuiz(@PathVariable String heroId) {
        Player player = playerService.restoreOrCreatePlayer(DEFAULT_PLAYER_USERNAME);
        String playerId = player.getId().toString();

        if (!quizService.isQuizComplete(playerId, heroId)) {
            return ResponseEntity.badRequest().build();
        }

        PlayerState updatedState = progressionService.unlockCard(player.getId(), heroId);
        return ResponseEntity.ok(updatedState);
    }
}
