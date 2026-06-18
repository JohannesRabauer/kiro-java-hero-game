package com.javahero.service;

import com.javahero.dto.PathProgress;
import com.javahero.dto.PlayerState;
import com.javahero.model.HeroCard;
import com.javahero.model.LearningPath;
import com.javahero.model.Player;
import com.javahero.repository.HeroCardRepository;
import com.javahero.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ProgressionService {

    private final PlayerRepository playerRepository;
    private final HeroCardRepository heroCardRepository;

    public ProgressionService(PlayerRepository playerRepository, HeroCardRepository heroCardRepository) {
        this.playerRepository = playerRepository;
        this.heroCardRepository = heroCardRepository;
    }

    public PlayerState getPlayerState(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));

        PlayerState state = new PlayerState();
        state.setPlayerId(player.getId());
        state.setTotalExperiencePoints(player.getTotalExperiencePoints());
        state.setOnboardingCompleted(player.isOnboardingCompleted());
        state.setCurrentPath(player.getCurrentPath());
        state.setUnlockedHeroIds(player.getUnlockedHeroIds());

        Map<LearningPath, PathProgress> pathProgressMap = new HashMap<>();
        for (LearningPath path : LearningPath.values()) {
            List<HeroCard> cardsInPath = heroCardRepository.findByLearningPathOrderByOrderInPath(path);
            int totalCards = cardsInPath.size();
            int unlockedCards = (int) cardsInPath.stream()
                    .filter(card -> player.getUnlockedHeroIds().contains(card.getId()))
                    .count();
            boolean completed = totalCards > 0 && unlockedCards == totalCards;
            pathProgressMap.put(path, new PathProgress(path, totalCards, unlockedCards, completed));
        }
        state.setPathProgressMap(pathProgressMap);

        String nextAvailableHeroId = getNextAvailableHero(playerId);
        state.setNextAvailableHeroId(nextAvailableHeroId);

        return state;
    }

    public PlayerState unlockCard(Long playerId, String heroId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));

        if (!isHeroAccessible(playerId, heroId)) {
            throw new IllegalStateException("Hero card is not accessible: " + heroId);
        }

        HeroCard heroCard = heroCardRepository.findById(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Hero card not found: " + heroId));

        // Add to unlocked set
        player.getUnlockedHeroIds().add(heroId);

        // Award XP based on the card's learning path
        int xpAward = heroCard.getLearningPath().getXpPerCard();
        player.setTotalExperiencePoints(player.getTotalExperiencePoints() + xpAward);

        // Update currentPath if needed: check if current path is fully completed
        // and advance to the next path
        LearningPath cardPath = heroCard.getLearningPath();
        List<HeroCard> cardsInCardPath = heroCardRepository.findByLearningPathOrderByOrderInPath(cardPath);
        boolean pathFullyCompleted = cardsInCardPath.stream()
                .allMatch(card -> player.getUnlockedHeroIds().contains(card.getId()));

        if (pathFullyCompleted) {
            LearningPath nextPath = getNextPath(cardPath);
            if (nextPath != null) {
                player.setCurrentPath(nextPath);
            }
        }

        player.setLastUpdatedAt(LocalDateTime.now());
        playerRepository.save(player);

        return getPlayerState(playerId);
    }

    public String getNextAvailableHero(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));

        Set<String> unlockedHeroIds = player.getUnlockedHeroIds();

        // Iterate paths in order: BRONZE -> SILVER -> GOLD -> SPRING_MASTER
        for (LearningPath path : LearningPath.values()) {
            List<HeroCard> cardsInPath = heroCardRepository.findByLearningPathOrderByOrderInPath(path);
            for (HeroCard card : cardsInPath) {
                if (!unlockedHeroIds.contains(card.getId())) {
                    return card.getId();
                }
            }
        }

        // All cards are unlocked
        return null;
    }

    public boolean isHeroAccessible(Long playerId, String heroId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));

        HeroCard heroCard = heroCardRepository.findById(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Hero card not found: " + heroId));

        Set<String> unlockedHeroIds = player.getUnlockedHeroIds();
        LearningPath heroPath = heroCard.getLearningPath();
        LearningPath currentPath = player.getCurrentPath();

        // Cards in paths beyond current+1 are NOT accessible
        if (heroPath.getOrder() > currentPath.getOrder() + 1) {
            return false;
        }

        // If the hero is in a path beyond the current path, it must be the first card
        // of the next path AND the previous path must be fully completed
        if (heroPath.getOrder() > currentPath.getOrder()) {
            // Must be first card of next path
            List<HeroCard> cardsInHeroPath = heroCardRepository.findByLearningPathOrderByOrderInPath(heroPath);
            if (cardsInHeroPath.isEmpty() || !cardsInHeroPath.get(0).getId().equals(heroId)) {
                return false;
            }
            // Previous path must be fully completed
            LearningPath previousPath = getPathByOrder(heroPath.getOrder() - 1);
            if (previousPath == null) {
                return false;
            }
            List<HeroCard> cardsInPreviousPath = heroCardRepository.findByLearningPathOrderByOrderInPath(previousPath);
            return cardsInPreviousPath.stream()
                    .allMatch(card -> unlockedHeroIds.contains(card.getId()));
        }

        // Hero is in the current path (or earlier completed path)
        // Check that all predecessors in the same path are unlocked
        List<HeroCard> cardsInPath = heroCardRepository.findByLearningPathOrderByOrderInPath(heroPath);
        for (HeroCard card : cardsInPath) {
            if (card.getId().equals(heroId)) {
                // All predecessors are unlocked, this card is accessible
                return true;
            }
            if (!unlockedHeroIds.contains(card.getId())) {
                // A predecessor is not unlocked
                return false;
            }
        }

        return false;
    }

    public void completeOnboarding(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));

        player.setOnboardingCompleted(true);
        player.setLastUpdatedAt(LocalDateTime.now());
        playerRepository.save(player);
    }

    private LearningPath getNextPath(LearningPath currentPath) {
        int nextOrder = currentPath.getOrder() + 1;
        return getPathByOrder(nextOrder);
    }

    private LearningPath getPathByOrder(int order) {
        for (LearningPath path : LearningPath.values()) {
            if (path.getOrder() == order) {
                return path;
            }
        }
        return null;
    }
}
