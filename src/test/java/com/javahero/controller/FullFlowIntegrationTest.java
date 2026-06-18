package com.javahero.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javahero.dto.AnswerRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class FullFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullFlow_learnQuizUnlockProgression() throws Exception {
        // Step 1: Verify initial state - no cards unlocked, next hero is bronze-variable
        mockMvc.perform(get("/api/player/state"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalExperiencePoints").value(0))
                .andExpect(jsonPath("$.nextAvailableHeroId").value("bronze-variable"));

        // Step 2: Answer all 3 questions correctly for bronze-variable
        // Question 1: bv-q1, correct answer: bv-q1-a
        answerCorrectly("bronze-variable", "bv-q1", "bv-q1-a");

        // Question 2: bv-q2, correct answer: bv-q2-b
        answerCorrectly("bronze-variable", "bv-q2", "bv-q2-b");

        // Question 3: bv-q3, correct answer: bv-q3-c
        answerCorrectly("bronze-variable", "bv-q3", "bv-q3-c");

        // Step 3: Complete the quiz - should unlock the card
        mockMvc.perform(post("/api/quiz/bronze-variable/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unlockedHeroIds").isArray())
                .andExpect(jsonPath("$.totalExperiencePoints").value(10))
                .andExpect(jsonPath("$.nextAvailableHeroId").value("bronze-string"));

        // Step 4: Verify player state reflects the unlock
        mockMvc.perform(get("/api/player/state"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalExperiencePoints").value(10))
                .andExpect(jsonPath("$.nextAvailableHeroId").value("bronze-string"));

        // Step 5: Verify collection now contains the unlocked card
        mockMvc.perform(get("/api/player/collection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("bronze-variable"));
    }

    private void answerCorrectly(String heroId, String questionId, String answerId) throws Exception {
        AnswerRequest request = new AnswerRequest(questionId, answerId);
        mockMvc.perform(post("/api/quiz/" + heroId + "/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true));
    }
}
