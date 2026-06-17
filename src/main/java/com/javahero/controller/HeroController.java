package com.javahero.controller;

import com.javahero.model.Hero;
import com.javahero.model.PlayerProgress;
import com.javahero.service.HeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HeroController {

    @Autowired
    private HeroService heroService;

    @Autowired
    private PlayerProgress playerProgress;

    @GetMapping("/hero/{id}/learn")
    public String learn(@PathVariable String id, Model model,
                        RedirectAttributes redirectAttributes) {
        return heroService.findById(id)
            .map(hero -> {
                model.addAttribute("hero", hero);
                model.addAttribute("playerProgress", playerProgress);
                return "hero-learn";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Hero not found: " + id);
                return "redirect:/roadmap";
            });
    }

    @GetMapping("/hero/{id}/quiz")
    public String quiz(@PathVariable String id,
                       @RequestParam(value = "error", required = false) String error,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        return heroService.findById(id)
            .map(hero -> {
                model.addAttribute("hero", hero);
                model.addAttribute("playerProgress", playerProgress);
                if (error != null) {
                    model.addAttribute("quizError", "Some answers were wrong. Try again!");
                }
                return "hero-quiz";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Hero not found: " + id);
                return "redirect:/roadmap";
            });
    }

    @GetMapping("/hero/{id}/unlock")
    public String unlock(@PathVariable String id, Model model,
                         RedirectAttributes redirectAttributes) {
        return heroService.findById(id)
            .map(hero -> {
                model.addAttribute("hero", hero);
                model.addAttribute("playerProgress", playerProgress);
                return "hero-unlock";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Hero not found: " + id);
                return "redirect:/roadmap";
            });
    }
}
