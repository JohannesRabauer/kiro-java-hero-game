package com.javahero.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class PlayerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPlayerState_returnsValidPlayerState() throws Exception {
        mockMvc.perform(get("/api/player/state"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").isNumber())
                .andExpect(jsonPath("$.totalExperiencePoints").value(0))
                .andExpect(jsonPath("$.onboardingCompleted").value(false))
                .andExpect(jsonPath("$.currentPath").value("BRONZE"))
                .andExpect(jsonPath("$.unlockedHeroIds").isArray())
                .andExpect(jsonPath("$.nextAvailableHeroId").value("bronze-variable"))
                .andExpect(jsonPath("$.pathProgressMap").isMap());
    }

    @Test
    void completeOnboarding_returns200() throws Exception {
        mockMvc.perform(post("/api/player/onboarding/complete"))
                .andExpect(status().isOk());

        // Verify onboarding is now completed
        mockMvc.perform(get("/api/player/state"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onboardingCompleted").value(true));
    }

    @Test
    void getCollection_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(get("/api/player/collection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
