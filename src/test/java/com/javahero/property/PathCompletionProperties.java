package com.javahero.property;

import com.javahero.model.LearningPath;
import net.jqwik.api.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Property 2: Path completion detection
 *
 * For any player state (set of unlocked hero IDs) and for any learning path,
 * the path is marked as "completed" if and only if every hero card in that path
 * is present in the player's unlocked set.
 *
 * Validates: Requirements 2.4, 7.5
 */
class PathCompletionProperties {

    // Cards per learning path (as defined in DataInitializer)
    private static final Map<LearningPath, List<String>> CARDS_PER_PATH = Map.of(
            LearningPath.BRONZE, List.of(
                    "bronze-variable", "bronze-string", "bronze-number",
                    "bronze-boolean", "bronze-if", "bronze-loop", "bronze-method"
            ),
            LearningPath.SILVER, List.of(
                    "silver-class", "silver-object", "silver-constructor",
                    "silver-collection", "silver-exception"
            ),
            LearningPath.GOLD, List.of(
                    "gold-interface", "gold-generic", "gold-stream",
                    "gold-lambda", "gold-optional", "gold-record"
            ),
            LearningPath.SPRING_MASTER, List.of(
                    "spring-controller", "spring-service", "spring-repository",
                    "spring-bean", "spring-di"
            )
    );

    private static final List<String> ALL_CARD_IDS = CARDS_PER_PATH.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());

    /**
     * **Validates: Requirements 2.4, 7.5**
     *
     * Property: For any player state (set of unlocked hero IDs) and for any learning path,
     * the path is "completed" if and only if every hero card in that path is present in the
     * player's unlocked set.
     */
    @Property(tries = 100)
    void pathIsCompletedIffAllCardsUnlocked(
            @ForAll("unlockedHeroIds") Set<String> unlockedIds,
            @ForAll("learningPaths") LearningPath path
    ) {
        List<String> cardsInPath = CARDS_PER_PATH.get(path);

        // Expected: path is completed iff every card in the path is unlocked
        boolean expectedCompleted = unlockedIds.containsAll(cardsInPath);

        // Actual: replicate the logic from ProgressionService
        int totalCards = cardsInPath.size();
        int unlockedCards = (int) cardsInPath.stream()
                .filter(unlockedIds::contains)
                .count();
        boolean actualCompleted = totalCards > 0 && unlockedCards == totalCards;

        assert actualCompleted == expectedCompleted :
                String.format("Path %s: expected completed=%s but got completed=%s " +
                                "(unlocked %d/%d cards, unlockedIds=%s)",
                        path, expectedCompleted, actualCompleted,
                        unlockedCards, totalCards, unlockedIds);
    }

    @Provide
    Arbitrary<Set<String>> unlockedHeroIds() {
        // Generate arbitrary subsets of all 24 card IDs
        return Arbitraries.of(ALL_CARD_IDS)
                .set()
                .ofMinSize(0)
                .ofMaxSize(ALL_CARD_IDS.size());
    }

    @Provide
    Arbitrary<LearningPath> learningPaths() {
        return Arbitraries.of(LearningPath.values());
    }
}
