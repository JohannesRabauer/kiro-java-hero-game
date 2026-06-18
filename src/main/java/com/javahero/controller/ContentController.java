package com.javahero.controller;

import com.javahero.model.HeroCard;
import com.javahero.model.HeroContent;
import com.javahero.model.LearningPath;
import com.javahero.model.Player;
import com.javahero.service.ContentService;
import com.javahero.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ContentController {

    private final ContentService contentService;
    private final PlayerService playerService;

    public ContentController(ContentService contentService, PlayerService playerService) {
        this.contentService = contentService;
        this.playerService = playerService;
    }

    /**
     * GET /api/content/{heroId} — Returns learning content for a specific hero card.
     * Returns 404 if the heroId is invalid or content not found.
     */
    @GetMapping("/content/{heroId}")
    public ResponseEntity<HeroContent> getHeroContent(@PathVariable String heroId) {
        try {
            HeroContent content = contentService.getHeroContent(heroId);
            return ResponseEntity.ok(content);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/paths — Returns all learning paths with their hero cards and lock/unlock status.
     * Uses the default player's unlocked set to determine lock status of each card.
     */
    @GetMapping("/paths")
    public ResponseEntity<List<PathWithCardsResponse>> getLearningPaths() {
        Map<LearningPath, List<HeroCard>> paths = contentService.getLearningPaths();

        // Get the default player's unlocked set for lock status
        Set<String> unlockedHeroIds = getDefaultPlayerUnlockedIds();

        List<PathWithCardsResponse> response = paths.entrySet().stream()
                .map(entry -> {
                    LearningPath path = entry.getKey();
                    List<HeroCardWithStatus> cards = entry.getValue().stream()
                            .map(card -> new HeroCardWithStatus(
                                    card.getId(),
                                    card.getName(),
                                    card.getConceptTitle(),
                                    card.getLearningPath(),
                                    card.getOrderInPath(),
                                    unlockedHeroIds.contains(card.getId())
                            ))
                            .collect(Collectors.toList());
                    return new PathWithCardsResponse(path, cards);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private Set<String> getDefaultPlayerUnlockedIds() {
        try {
            Player player = playerService.restoreOrCreatePlayer("default");
            return player.getUnlockedHeroIds();
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    /**
     * Response DTO for the paths endpoint, containing a learning path and its cards with status.
     */
    public record PathWithCardsResponse(
            LearningPath path,
            List<HeroCardWithStatus> cards
    ) {}

    /**
     * DTO representing a hero card along with its lock/unlock status.
     */
    public record HeroCardWithStatus(
            String id,
            String name,
            String conceptTitle,
            LearningPath learningPath,
            int orderInPath,
            boolean unlocked
    ) {}
}
