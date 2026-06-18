package com.javahero.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Transactional
class ContentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getHeroContent_validId_returnsContent() throws Exception {
        mockMvc.perform(get("/api/content/bronze-variable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.heroId").value("bronze-variable"))
                .andExpect(jsonPath("$.whatItIs").isNotEmpty())
                .andExpect(jsonPath("$.whyItMatters").isNotEmpty())
                .andExpect(jsonPath("$.howToUseIt").isNotEmpty())
                .andExpect(jsonPath("$.codeExamples").isArray())
                .andExpect(jsonPath("$.codeExamples.length()").value(1));
    }

    @Test
    void getHeroContent_invalidId_returns404() throws Exception {
        mockMvc.perform(get("/api/content/invalid-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLearningPaths_returnsAllFourPaths() throws Exception {
        mockMvc.perform(get("/api/paths"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].path").value("BRONZE"))
                .andExpect(jsonPath("$[0].cards").isArray())
                .andExpect(jsonPath("$[0].cards.length()").value(7));
    }
}
