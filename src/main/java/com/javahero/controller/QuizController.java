package com.javahero.controller;

import com.javahero.model.Hero;
import com.javahero.model.PlayerProgress;
import com.javahero.service.HeroService;
import com.javahero.service.ProgressService;
import com.javahero.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class QuizController {

    @Autowired
    private HeroService heroService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private ProgressService progressService;

    @Autowired
    private PlayerProgress playerProgress;

    @PostMapping("/hero/{id}/quiz")
    public String submitQuiz(@PathVariable String id,
                             @RequestParam Map<String, String> allParams) {
        Optional<Hero> heroOpt = heroService.findById(id);
        if (heroOpt.isEmpty()) {
            return "redirect:/roadmap";
        }

        Hero hero = heroOpt.get();

        // Extract answer_0, answer_1, answer_2 ... into a map of index -> chosen answer
        Map<String, Integer> answers = new HashMap<>();
        for (int i = 0; i < hero.getQuestions().size(); i++) {
            String paramKey = "answer_" + i;
            String value = allParams.get(paramKey);
            if (value != null) {
                try {
                    answers.put(String.valueOf(i), Integer.parseInt(value));
                } catch (NumberFormatException ignored) {
                    // treat as wrong answer
                }
            }
        }

        boolean allCorrect = quizService.validateAnswers(hero, answers);
        if (allCorrect) {
            progressService.completeHero(playerProgress, id);
            return "redirect:/hero/" + id + "/unlock";
        } else {
            return "redirect:/hero/" + id + "/quiz?error=true";
        }
    }
}
