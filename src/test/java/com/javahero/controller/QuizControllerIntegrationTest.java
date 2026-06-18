package com.javahero.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javahero.dto.AnswerRequest;
import com.javahero.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Transactional
class QuizControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QuizService quizService;

    @BeforeEach
    void resetQuizState() {
        // Reset in-memory quiz session state to ensure test isolation
        quizService.resetQuizSession("1", "bronze-variable");
        quizService.resetQuizSession("2", "bronze-variable");
    }

    @Test
    void getQuestions_returnsQuestionsWithoutCorrectOptionId() throws Exception {
        mockMvc.perform(get("/api/quiz/bronze-variable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].questionText").isNotEmpty())
                .andExpect(jsonPath("$[0].options").isArray())
                .andExpect(jsonPath("$[0].correctOptionId").doesNotExist());
    }

    @Test
    void evaluateAnswer_correctAnswer_returnsTrue() throws Exception {
        AnswerRequest request = new AnswerRequest("bv-q1", "bv-q1-a");

        mockMvc.perform(post("/api/quiz/bronze-variable/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true));
    }

    @Test
    void evaluateAnswer_wrongAnswer_returnsFalse() throws Exception {
        AnswerRequest request = new AnswerRequest("bv-q1", "bv-q1-c");

        mockMvc.perform(post("/api/quiz/bronze-variable/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(false));
    }

    @Test
    void completeQuiz_beforeAnsweringAll_returns400() throws Exception {
        mockMvc.perform(post("/api/quiz/bronze-variable/complete"))
                .andExpect(status().isBadRequest());
    }
}
