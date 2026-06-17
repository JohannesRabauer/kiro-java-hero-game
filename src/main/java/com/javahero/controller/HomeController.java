package com.javahero.controller;

import com.javahero.model.PlayerProgress;
import com.javahero.service.HeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private HeroService heroService;

    @Autowired
    private PlayerProgress playerProgress;

    @GetMapping("/")
    public String index() {
        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/roadmap")
    public String roadmap(Model model) {
        model.addAttribute("heroesByPath", heroService.getHeroesByPath());
        model.addAttribute("playerProgress", playerProgress);
        model.addAttribute("totalHeroes", heroService.getAllHeroesSorted().size());
        return "roadmap";
    }
}
