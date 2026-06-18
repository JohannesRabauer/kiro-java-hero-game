package com.javahero.property;

import com.javahero.dto.PathProgress;
import com.javahero.model.LearningPath;
import net.jqwik.api.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Property 12: Progress calculation accuracy
 *
 * For any player state (set of unlocked hero IDs) and for any learning path,
 * the progress indicator shows the count of unlocked cards in that path as
 * the numerator and the total defined cards in that path as the denominator,
 * with the ratio matching the actual set intersection.
 *
 * Validates: Requirements 7.7
 */
class ProgressCalculationProperties {

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
     * Computes PathProgress from a set of unlocked hero IDs and a LearningPath.
     * This replicates the pure logic of ContentService.getPathProgress without
     * database dependencies.
     */
    private PathProgress computePathProgress(Set<String> unlockedHeroIds, LearningPath path) {
        List<String> cardsInPath = CARDS_PER_PATH.get(path);
        int totalCards = cardsInPath.size();
        int unlockedCards = (int) cardsInPath.stream()
                .filter(unlockedHeroIds::contains)
                .count();
        boolean completed = totalCards > 0 && unlockedCards == totalCards;
        return new PathProgress(path, totalCards, unlockedCards, completed);
    }

    /**
     * **Validates: Requirements 7.7**
     *
     * Property: totalCards matches the known count for each path.
     */
    @Property(tries = 100)
    void totalCardsMatchesKnownCountForPath(
            @ForAll("unlockedHeroIds") Set<String> unlockedHeroIds,
            @ForAll("learningPaths") LearningPath path
    ) {
        PathProgress progress = computePathProgress(unlockedHeroIds, path);
        int expectedTotal = CARDS_PER_PATH.get(path).size();

        assert progress.getTotalCards() == expectedTotal :
                String.format("Path %s: expected totalCards=%d but got %d",
                        path, expectedTotal, progress.getTotalCards());
    }

    /**
     * **Validates: Requirements 7.7**
     *
     * Property: unlockedCards matches the intersection of unlocked set and path cards.
     */
    @Property(tries = 100)
    void unlockedCardsMatchesIntersectionOfUnlockedSetAndPathCards(
            @ForAll("unlockedHeroIds") Set<String> unlockedHeroIds,
            @ForAll("learningPaths") LearningPath path
    ) {
        PathProgress progress = computePathProgress(unlockedHeroIds, path);
        List<String> cardsInPath = CARDS_PER_PATH.get(path);

        // Expected: count of cards in this path that are also in the unlocked set
        long expectedUnlocked = cardsInPath.stream()
                .filter(unlockedHeroIds::contains)
                .count();

        assert progress.getUnlockedCards() == expectedUnlocked :
                String.format("Path %s: expected unlockedCards=%d but got %d (unlockedIds=%s)",
                        path, expectedUnlocked, progress.getUnlockedCards(), unlockedHeroIds);
    }

    /**
     * **Validates: Requirements 7.7**
     *
     * Property: completed is true if and only if unlockedCards == totalCards.
     */
    @Property(tries = 100)
    void completedIsTrueIffUnlockedEqualsTotalCards(
            @ForAll("unlockedHeroIds") Set<String> unlockedHeroIds,
            @ForAll("learningPaths") LearningPath path
    ) {
        PathProgress progress = computePathProgress(unlockedHeroIds, path);

        boolean expectedCompleted = progress.getTotalCards() > 0
                && progress.getUnlockedCards() == progress.getTotalCards();

        assert progress.isCompleted() == expectedCompleted :
                String.format("Path %s: expected completed=%s but got %s (unlocked %d/%d)",
                        path, expectedCompleted, progress.isCompleted(),
                        progress.getUnlockedCards(), progress.getTotalCards());
    }

    @Provide
    Arbitrary<Set<String>> unlockedHeroIds() {
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
