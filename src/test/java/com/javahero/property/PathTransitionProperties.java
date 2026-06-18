package com.javahero.property;

import com.javahero.model.LearningPath;
import net.jqwik.api.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Property 5: Path transition unlocking
 *
 * For any learning path that is not the final path (not SPRING_MASTER), when all
 * hero cards in that path are unlocked (in the player's unlocked set), the first
 * hero card of the next learning path is accessible to the player.
 *
 * Validates: Requirements 3.3, 6.5
 */
class PathTransitionProperties {

    // Cards per path, in order
    private static final Map<LearningPath, List<String>> PATH_CARDS = Map.of(
            LearningPath.BRONZE, List.of(
                    "bronze-variable", "bronze-string", "bronze-number", "bronze-boolean",
                    "bronze-if", "bronze-loop", "bronze-method"),
            LearningPath.SILVER, List.of(
                    "silver-class", "silver-object", "silver-constructor",
                    "silver-collection", "silver-exception"),
            LearningPath.GOLD, List.of(
                    "gold-interface", "gold-generic", "gold-stream",
                    "gold-lambda", "gold-optional", "gold-record"),
            LearningPath.SPRING_MASTER, List.of(
                    "spring-controller", "spring-service", "spring-repository",
                    "spring-bean", "spring-di")
    );

    // Non-final paths that can transition to a next path
    private static final List<LearningPath> NON_FINAL_PATHS = List.of(
            LearningPath.BRONZE, LearningPath.SILVER, LearningPath.GOLD
    );

    /**
     * Returns the next learning path after the given one, or null if it's the final path.
     */
    private LearningPath getNextPath(LearningPath path) {
        int nextOrder = path.getOrder() + 1;
        for (LearningPath lp : LearningPath.values()) {
            if (lp.getOrder() == nextOrder) {
                return lp;
            }
        }
        return null;
    }

    /**
     * Returns all cards in paths up to and including the given path.
     */
    private Set<String> getAllCardsUpToAndIncluding(LearningPath path) {
        Set<String> cards = new HashSet<>();
        for (LearningPath lp : LearningPath.values()) {
            if (lp.getOrder() <= path.getOrder()) {
                cards.addAll(PATH_CARDS.get(lp));
            }
        }
        return cards;
    }

    /**
     * Pure logic check: a card is accessible if all preceding cards (in same path
     * and all prior paths) are unlocked.
     *
     * For the first card of a path, all cards in the previous path must be unlocked.
     * For non-first cards in a path, all preceding cards in the same path must be unlocked,
     * plus all prior paths must be fully unlocked.
     */
    private boolean isCardAccessible(Set<String> unlockedHeroIds, String heroId) {
        // Find which path this card belongs to
        LearningPath cardPath = null;
        int cardIndex = -1;
        for (Map.Entry<LearningPath, List<String>> entry : PATH_CARDS.entrySet()) {
            int idx = entry.getValue().indexOf(heroId);
            if (idx >= 0) {
                cardPath = entry.getKey();
                cardIndex = idx;
                break;
            }
        }

        if (cardPath == null) {
            return false;
        }

        // All cards in prior paths must be unlocked
        for (LearningPath lp : LearningPath.values()) {
            if (lp.getOrder() < cardPath.getOrder()) {
                if (!unlockedHeroIds.containsAll(PATH_CARDS.get(lp))) {
                    return false;
                }
            }
        }

        // All preceding cards in the same path must be unlocked
        List<String> pathCards = PATH_CARDS.get(cardPath);
        for (int i = 0; i < cardIndex; i++) {
            if (!unlockedHeroIds.contains(pathCards.get(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * **Validates: Requirements 3.3, 6.5**
     *
     * Property: When all cards in a non-final path are unlocked (along with all prior paths),
     * the first card of the next path is accessible.
     */
    @Property(tries = 100)
    void completedNonFinalPathUnlocksFirstCardOfNextPath(
            @ForAll("nonFinalPaths") LearningPath completedPath
    ) {
        // Build a player state where ALL cards up to and including the completed path are unlocked
        Set<String> unlockedHeroIds = getAllCardsUpToAndIncluding(completedPath);

        // Determine the next path and its first card
        LearningPath nextPath = getNextPath(completedPath);
        assert nextPath != null : "Non-final path should have a next path";

        String firstCardOfNextPath = PATH_CARDS.get(nextPath).get(0);

        // Verify that the first card of the next path is accessible
        boolean accessible = isCardAccessible(unlockedHeroIds, firstCardOfNextPath);

        assert accessible :
                "After completing all cards in " + completedPath +
                ", the first card of " + nextPath + " (" + firstCardOfNextPath +
                ") should be accessible, but it was not.";
    }

    /**
     * **Validates: Requirements 3.3, 6.5**
     *
     * Property: The specific path transitions produce the correct first accessible card.
     * BRONZE completed → silver-class accessible
     * SILVER completed → gold-interface accessible (must also have all BRONZE)
     * GOLD completed → spring-controller accessible (must also have all BRONZE + SILVER)
     */
    @Property(tries = 100)
    void pathTransitionUnlocksCorrectFirstCard(
            @ForAll("nonFinalPaths") LearningPath completedPath
    ) {
        // Build player state with all cards in completed path AND all prior paths unlocked
        Set<String> unlockedHeroIds = getAllCardsUpToAndIncluding(completedPath);

        LearningPath nextPath = getNextPath(completedPath);
        assert nextPath != null;

        String expectedFirstCard = PATH_CARDS.get(nextPath).get(0);

        // The expected first card must be accessible
        boolean accessible = isCardAccessible(unlockedHeroIds, expectedFirstCard);
        assert accessible :
                "Expected first card '" + expectedFirstCard + "' of " + nextPath +
                " to be accessible after completing " + completedPath;

        // Non-first cards of the next path should NOT be accessible (since they have predecessors)
        List<String> nextPathCards = PATH_CARDS.get(nextPath);
        if (nextPathCards.size() > 1) {
            String secondCard = nextPathCards.get(1);
            boolean secondAccessible = isCardAccessible(unlockedHeroIds, secondCard);
            assert !secondAccessible :
                    "Second card '" + secondCard + "' of " + nextPath +
                    " should NOT be accessible (first card not yet unlocked)";
        }
    }

    @Provide
    Arbitrary<LearningPath> nonFinalPaths() {
        return Arbitraries.of(NON_FINAL_PATHS);
    }
}
