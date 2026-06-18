package com.javahero.service;

import com.javahero.dto.PathProgress;
import com.javahero.model.HeroCard;
import com.javahero.model.HeroContent;
import com.javahero.model.LearningPath;
import com.javahero.model.Player;
import com.javahero.repository.HeroCardRepository;
import com.javahero.repository.HeroContentRepository;
import com.javahero.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentService {

    private final HeroCardRepository heroCardRepository;
    private final HeroContentRepository heroContentRepository;
    private final PlayerRepository playerRepository;

    public ContentService(HeroCardRepository heroCardRepository,
                          HeroContentRepository heroContentRepository,
                          PlayerRepository playerRepository) {
        this.heroCardRepository = heroCardRepository;
        this.heroContentRepository = heroContentRepository;
        this.playerRepository = playerRepository;
    }

    /**
     * Returns structured learning content for a specific hero card.
     *
     * @param heroId the hero card identifier
     * @return the HeroContent entity with whatItIs, whyItMatters, howToUseIt, and codeExamples
     * @throws IllegalArgumentException if no content found for the given heroId
     */
    public HeroContent getHeroContent(String heroId) {
        return heroContentRepository.findById(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Hero content not found: " + heroId));
    }

    /**
     * Returns all learning paths with their hero cards, ordered by path order then card order.
     *
     * @return a linked map of LearningPath to list of HeroCards, maintaining path order
     */
    public Map<LearningPath, List<HeroCard>> getLearningPaths() {
        List<HeroCard> allCards = heroCardRepository.findAll();

        return allCards.stream()
                .sorted(Comparator.comparingInt((HeroCard card) -> card.getLearningPath().getOrder())
                        .thenComparingInt(HeroCard::getOrderInPath))
                .collect(Collectors.groupingBy(
                        HeroCard::getLearningPath,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    /**
     * Returns progress within a specific learning path for the given player.
     *
     * @param playerId the player's database ID
     * @param path     the learning path to check progress for
     * @return a PathProgress DTO with unlocked/total counts and completion status
     * @throws IllegalArgumentException if the player is not found
     */
    public PathProgress getPathProgress(Long playerId, LearningPath path) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));

        List<HeroCard> cardsInPath = heroCardRepository.findByLearningPathOrderByOrderInPath(path);
        int totalCards = cardsInPath.size();

        Set<String> unlockedHeroIds = player.getUnlockedHeroIds();
        int unlockedCards = (int) cardsInPath.stream()
                .filter(card -> unlockedHeroIds.contains(card.getId()))
                .count();

        boolean completed = totalCards > 0 && unlockedCards == totalCards;

        return new PathProgress(path, totalCards, unlockedCards, completed);
    }
}
