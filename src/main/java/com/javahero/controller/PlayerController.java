package com.javahero.controller;

import com.javahero.dto.PlayerState;
import com.javahero.model.HeroCard;
import com.javahero.model.Player;
import com.javahero.repository.HeroCardRepository;
import com.javahero.service.PlayerService;
import com.javahero.service.ProgressionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private static final String DEFAULT_PLAYER_USERNAME = "default-player";

    private final ProgressionService progressionService;
    private final PlayerService playerService;
    private final HeroCardRepository heroCardRepository;

    public PlayerController(ProgressionService progressionService,
                            PlayerService playerService,
                            HeroCardRepository heroCardRepository) {
        this.progressionService = progressionService;
        this.playerService = playerService;
        this.heroCardRepository = heroCardRepository;
    }

    @GetMapping("/state")
    public ResponseEntity<PlayerState> getPlayerState() {
        Player player = playerService.restoreOrCreatePlayer(DEFAULT_PLAYER_USERNAME);
        PlayerState state = progressionService.getPlayerState(player.getId());
        return ResponseEntity.ok(state);
    }

    @PostMapping("/onboarding/complete")
    public ResponseEntity<Void> completeOnboarding() {
        Player player = playerService.restoreOrCreatePlayer(DEFAULT_PLAYER_USERNAME);
        progressionService.completeOnboarding(player.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/collection")
    public ResponseEntity<List<HeroCard>> getCollection() {
        Player player = playerService.restoreOrCreatePlayer(DEFAULT_PLAYER_USERNAME);
        Set<String> unlockedHeroIds = player.getUnlockedHeroIds();

        List<HeroCard> unlockedCards = unlockedHeroIds.stream()
                .map(heroCardRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toList());

        return ResponseEntity.ok(unlockedCards);
    }
}
