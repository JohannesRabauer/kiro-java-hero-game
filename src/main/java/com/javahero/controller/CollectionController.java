package com.javahero.controller;

import com.javahero.model.PlayerProgress;
import com.javahero.service.HeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CollectionController {

    @Autowired
    private HeroService heroService;

    @Autowired
    private PlayerProgress playerProgress;

    @GetMapping("/collection")
    public String collection(Model model) {
        model.addAttribute("allHeroes", heroService.getAllHeroesSorted());
        model.addAttribute("heroesByPath", heroService.getHeroesByPath());
        model.addAttribute("playerProgress", playerProgress);
        return "collection";
    }
}
