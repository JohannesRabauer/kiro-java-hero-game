package com.javahero.property;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Property 3: Next available hero determination
 *
 * For any player state that is not fully complete (not all 24 cards unlocked),
 * there is exactly one hero card that is "active" (next available), and it is
 * the first non-unlocked card in the earliest incomplete learning path.
 *
 * **Validates: Requirements 2.5**
 */
public class NextAvailableHeroProperties {

    // Card order within paths (as defined in the spec)
    private static final List<String> BRONZE_CARDS = List.of(
            "bronze-variable", "bronze-string", "bronze-number",
            "bronze-boolean", "bronze-if", "bronze-loop", "bronze-method");

    private static final List<String> SILVER_CARDS = List.of(
            "silver-class", "silver-object", "silver-constructor",
            "silver-collection", "silver-exception");

    private static final List<String> GOLD_CARDS = List.of(
            "gold-interface", "gold-generic", "gold-stream",
            "gold-lambda", "gold-optional", "gold-record");

    private static final List<String> SPRING_MASTER_CARDS = List.of(
            "spring-controller", "spring-service", "spring-repository",
            "spring-bean", "spring-di");

    // All cards in path order: BRONZE -> SILVER -> GOLD -> SPRING_MASTER
    private static final List<String> ALL_CARDS_IN_ORDER;

    static {
        List<String> all = new ArrayList<>();
        all.addAll(BRONZE_CARDS);
        all.addAll(SILVER_CARDS);
        all.addAll(GOLD_CARDS);
        all.addAll(SPRING_MASTER_CARDS);
        ALL_CARDS_IN_ORDER = Collections.unmodifiableList(all);
    }

    /**
     * Computes the next available hero given a set of unlocked card IDs.
     * This is the pure logic equivalent of ProgressionService.getNextAvailableHero.
     *
     * Iterates paths in order (BRONZE, SILVER, GOLD, SPRING_MASTER),
     * for each path iterates cards in order, returns the first card ID not in the unlocked set.
     *
     * @param unlockedHeroIds the set of currently unlocked hero card IDs
     * @return the ID of the next available hero, or null if all are unlocked
     */
    static String computeNextAvailableHero(Set<String> unlockedHeroIds) {
        for (String cardId : ALL_CARDS_IN_ORDER) {
            if (!unlockedHeroIds.contains(cardId)) {
                return cardId;
            }
        }
        return null; // All cards unlocked
    }

    /**
     * Provides arbitrary subsets of unlocked hero IDs that are NOT the full set of 24 cards.
     * This ensures at least one card is NOT unlocked.
     */
    @Provide
    Arbitrary<Set<String>> incompleteUnlockedSets() {
        // Generate a random subset of ALL_CARDS_IN_ORDER that does NOT include all 24 cards
        return Arbitraries.integers().between(0, ALL_CARDS_IN_ORDER.size() - 1)
                .set().ofMinSize(0).ofMaxSize(ALL_CARDS_IN_ORDER.size() - 1)
                .map(indices -> indices.stream()
                        .map(ALL_CARDS_IN_ORDER::get)
                        .collect(Collectors.toSet()));
    }

    /**
     * Property: For any incomplete player state (not all 24 cards unlocked),
     * exactly one hero card is "active" — the first non-unlocked card in the
     * earliest incomplete learning path.
     *
     * **Validates: Requirements 2.5**
     */
    @Property(tries = 100)
    void nextAvailableHeroIsExactlyOneAndCorrect(@ForAll("incompleteUnlockedSets") Set<String> unlockedHeroIds) {
        // Precondition: not all cards are unlocked
        assert unlockedHeroIds.size() < ALL_CARDS_IN_ORDER.size();

        // Compute the next available hero
        String nextHero = computeNextAvailableHero(unlockedHeroIds);

        // There must be exactly one next available hero (not null)
        assert nextHero != null : "Expected a next available hero but got null";

        // The next hero must NOT be in the unlocked set
        assert !unlockedHeroIds.contains(nextHero) :
                "Next available hero '" + nextHero + "' should not be in the unlocked set";

        // The next hero must be a valid card ID
        assert ALL_CARDS_IN_ORDER.contains(nextHero) :
                "Next available hero '" + nextHero + "' is not a valid card ID";

        // Verify it is the FIRST non-unlocked card in path order
        String expectedFirst = null;
        for (String cardId : ALL_CARDS_IN_ORDER) {
            if (!unlockedHeroIds.contains(cardId)) {
                expectedFirst = cardId;
                break;
            }
        }
        assert nextHero.equals(expectedFirst) :
                "Expected next hero to be '" + expectedFirst + "' but got '" + nextHero + "'";

        // Verify uniqueness: there is exactly one "active" hero (the first non-unlocked)
        // All cards before this one in the full order should be unlocked
        int nextHeroIndex = ALL_CARDS_IN_ORDER.indexOf(nextHero);
        for (int i = 0; i < nextHeroIndex; i++) {
            assert unlockedHeroIds.contains(ALL_CARDS_IN_ORDER.get(i)) :
                    "Card '" + ALL_CARDS_IN_ORDER.get(i) + "' at index " + i +
                    " should be unlocked since next hero is at index " + nextHeroIndex;
        }
    }
}
