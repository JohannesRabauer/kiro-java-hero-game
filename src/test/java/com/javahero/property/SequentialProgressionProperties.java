package com.javahero.property;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Property 4: Sequential progression invariant
 *
 * For any player state (set of unlocked hero IDs) and for any hero card that is
 * "accessible" to the player:
 * 1. All preceding hero cards in the same learning path must be in the player's unlocked set
 * 2. No hero card in a learning path beyond the player's current active path + 1 is accessible
 *
 * Validates: Requirements 3.2, 3.4, 6.2, 6.4
 */
class SequentialProgressionProperties {

    // Learning paths in order with their cards
    enum Path {
        BRONZE(1), SILVER(2), GOLD(3), SPRING_MASTER(4);

        final int order;

        Path(int order) {
            this.order = order;
        }
    }

    private static final Map<Path, List<String>> PATH_CARDS = new LinkedHashMap<>();

    static {
        PATH_CARDS.put(Path.BRONZE, List.of(
                "bronze-variable", "bronze-string", "bronze-number", "bronze-boolean",
                "bronze-if", "bronze-loop", "bronze-method"
        ));
        PATH_CARDS.put(Path.SILVER, List.of(
                "silver-class", "silver-object", "silver-constructor",
                "silver-collection", "silver-exception"
        ));
        PATH_CARDS.put(Path.GOLD, List.of(
                "gold-interface", "gold-generic", "gold-stream",
                "gold-lambda", "gold-optional", "gold-record"
        ));
        PATH_CARDS.put(Path.SPRING_MASTER, List.of(
                "spring-controller", "spring-service", "spring-repository",
                "spring-bean", "spring-di"
        ));
    }

    private static final List<String> ALL_HERO_IDS = PATH_CARDS.values().stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());

    /**
     * Determines the current active path based on unlocked cards.
     * The current path is the earliest path that is not fully completed.
     * If all paths are complete, returns SPRING_MASTER.
     */
    private Path getCurrentPath(Set<String> unlockedHeroIds) {
        for (Path path : Path.values()) {
            List<String> cardsInPath = PATH_CARDS.get(path);
            boolean pathComplete = cardsInPath.stream().allMatch(unlockedHeroIds::contains);
            if (!pathComplete) {
                return path;
            }
        }
        return Path.SPRING_MASTER;
    }

    /**
     * Determines if a hero card is accessible given the player's unlocked set and current path.
     * Mirrors the ProgressionService logic:
     * - Cards in paths beyond currentPath.order + 1 are NOT accessible
     * - Cards in the next path (currentPath.order + 1) are accessible only if they are the
     *   first card AND the previous path is fully completed
     * - Cards in current or earlier paths are accessible if all predecessors are unlocked
     */
    private boolean isAccessible(String heroId, Set<String> unlockedHeroIds, Path currentPath) {
        Path heroPath = getPathForHero(heroId);
        if (heroPath == null) {
            return false;
        }

        // Cards in paths beyond current+1 are NOT accessible
        if (heroPath.order > currentPath.order + 1) {
            return false;
        }

        // If the hero is in a path beyond the current path
        if (heroPath.order > currentPath.order) {
            List<String> cardsInHeroPath = PATH_CARDS.get(heroPath);
            // Must be the first card of that path
            if (!cardsInHeroPath.get(0).equals(heroId)) {
                return false;
            }
            // Previous path must be fully completed
            Path previousPath = getPathByOrder(heroPath.order - 1);
            if (previousPath == null) {
                return false;
            }
            List<String> cardsInPreviousPath = PATH_CARDS.get(previousPath);
            return cardsInPreviousPath.stream().allMatch(unlockedHeroIds::contains);
        }

        // Hero is in the current path or earlier completed path
        // Check that all predecessors in the same path are unlocked
        List<String> cardsInPath = PATH_CARDS.get(heroPath);
        for (String card : cardsInPath) {
            if (card.equals(heroId)) {
                return true; // All predecessors are unlocked
            }
            if (!unlockedHeroIds.contains(card)) {
                return false; // A predecessor is not unlocked
            }
        }

        return false;
    }

    private Path getPathForHero(String heroId) {
        for (Map.Entry<Path, List<String>> entry : PATH_CARDS.entrySet()) {
            if (entry.getValue().contains(heroId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Path getPathByOrder(int order) {
        for (Path path : Path.values()) {
            if (path.order == order) {
                return path;
            }
        }
        return null;
    }

    /**
     * Gets all predecessors of a hero card within the same path.
     */
    private List<String> getPredecessorsInPath(String heroId) {
        Path path = getPathForHero(heroId);
        if (path == null) {
            return Collections.emptyList();
        }
        List<String> cardsInPath = PATH_CARDS.get(path);
        int index = cardsInPath.indexOf(heroId);
        if (index <= 0) {
            return Collections.emptyList();
        }
        return cardsInPath.subList(0, index);
    }

    /**
     * **Validates: Requirements 3.2, 3.4, 6.2, 6.4**
     *
     * Property: For any accessible card, all predecessors in the same learning path
     * must be in the player's unlocked set.
     */
    @Property(tries = 100)
    void accessibleCardHasAllPredecessorsUnlocked(
            @ForAll("validPlayerStates") Set<String> unlockedHeroIds
    ) {
        Path currentPath = getCurrentPath(unlockedHeroIds);

        for (String heroId : ALL_HERO_IDS) {
            if (isAccessible(heroId, unlockedHeroIds, currentPath)) {
                List<String> predecessors = getPredecessorsInPath(heroId);
                for (String predecessor : predecessors) {
                    assert unlockedHeroIds.contains(predecessor) :
                            "Card '" + heroId + "' is accessible but predecessor '" + predecessor +
                                    "' is NOT unlocked. Unlocked set: " + unlockedHeroIds;
                }
            }
        }
    }

    /**
     * **Validates: Requirements 3.2, 3.4, 6.2, 6.4**
     *
     * Property: No hero card in a learning path beyond the player's current active path + 1
     * is accessible.
     */
    @Property(tries = 100)
    void noCardBeyondCurrentPathPlusOneIsAccessible(
            @ForAll("validPlayerStates") Set<String> unlockedHeroIds
    ) {
        Path currentPath = getCurrentPath(unlockedHeroIds);

        for (String heroId : ALL_HERO_IDS) {
            Path heroPath = getPathForHero(heroId);
            if (heroPath != null && heroPath.order > currentPath.order + 1) {
                boolean accessible = isAccessible(heroId, unlockedHeroIds, currentPath);
                assert !accessible :
                        "Card '" + heroId + "' in path " + heroPath +
                                " (order " + heroPath.order + ") is accessible, but current path is " +
                                currentPath + " (order " + currentPath.order +
                                "). Cards beyond currentPath+1 should never be accessible.";
            }
        }
    }

    /**
     * Generates valid player states: unlocked sets that respect the sequential constraint.
     * A valid state has consecutive cards from the start of each path unlocked,
     * and only paths up to the current active path can have unlocked cards.
     */
    @Provide
    Arbitrary<Set<String>> validPlayerStates() {
        // Generate how many cards are unlocked in each path (0..max for that path)
        // Constraint: If path N has < all cards unlocked, paths N+1..4 must have 0 unlocked
        return Arbitraries.integers().between(0, PATH_CARDS.get(Path.BRONZE).size())
                .flatMap(bronzeCount ->
                        Arbitraries.integers().between(0, PATH_CARDS.get(Path.SILVER).size())
                                .flatMap(silverCount ->
                                        Arbitraries.integers().between(0, PATH_CARDS.get(Path.GOLD).size())
                                                .flatMap(goldCount ->
                                                        Arbitraries.integers().between(0, PATH_CARDS.get(Path.SPRING_MASTER).size())
                                                                .map(springCount -> {
                                                                    // Enforce sequential constraint across paths
                                                                    int actualBronze = bronzeCount;
                                                                    int actualSilver = silverCount;
                                                                    int actualGold = goldCount;
                                                                    int actualSpring = springCount;

                                                                    // Silver only if bronze fully complete
                                                                    if (actualBronze < PATH_CARDS.get(Path.BRONZE).size()) {
                                                                        actualSilver = 0;
                                                                    }
                                                                    // Gold only if silver fully complete
                                                                    if (actualSilver < PATH_CARDS.get(Path.SILVER).size()) {
                                                                        actualGold = 0;
                                                                    }
                                                                    // Spring only if gold fully complete
                                                                    if (actualGold < PATH_CARDS.get(Path.GOLD).size()) {
                                                                        actualSpring = 0;
                                                                    }

                                                                    Set<String> unlocked = new HashSet<>();
                                                                    addFirstN(unlocked, Path.BRONZE, actualBronze);
                                                                    addFirstN(unlocked, Path.SILVER, actualSilver);
                                                                    addFirstN(unlocked, Path.GOLD, actualGold);
                                                                    addFirstN(unlocked, Path.SPRING_MASTER, actualSpring);
                                                                    return unlocked;
                                                                })
                                                )
                                )
                );
    }

    private void addFirstN(Set<String> set, Path path, int n) {
        List<String> cards = PATH_CARDS.get(path);
        for (int i = 0; i < n && i < cards.size(); i++) {
            set.add(cards.get(i));
        }
    }
}
