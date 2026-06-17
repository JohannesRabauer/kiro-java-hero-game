package com.javahero.service;

import com.javahero.data.HeroDataLoader;
import com.javahero.model.Hero;
import com.javahero.model.LearningPath;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HeroService {

    private final List<Hero> allHeroes;

    public HeroService(HeroDataLoader dataLoader) {
        this.allHeroes = dataLoader.loadAllHeroes().stream()
            .sorted(Comparator.comparingInt((Hero h) -> h.getLearningPath().getOrder())
                .thenComparingInt(Hero::getOrder))
            .toList();
    }

    public List<Hero> getAllHeroesSorted() {
        return allHeroes;
    }

    public Optional<Hero> findById(String id) {
        return allHeroes.stream()
            .filter(h -> h.getId().equals(id))
            .findFirst();
    }

    public Map<LearningPath, List<Hero>> getHeroesByPath() {
        Map<LearningPath, List<Hero>> result = new LinkedHashMap<>();
        for (LearningPath path : LearningPath.values()) {
            List<Hero> heroes = allHeroes.stream()
                .filter(h -> h.getLearningPath() == path)
                .toList();
            result.put(path, heroes);
        }
        return result;
    }
}
