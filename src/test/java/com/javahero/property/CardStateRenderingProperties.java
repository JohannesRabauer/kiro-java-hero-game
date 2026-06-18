package com.javahero.property;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Property 1: Card state rendering consistency
 *
 * For any player state (set of unlocked hero IDs) and for any hero card ID,
 * the card's display state is "completed" if and only if the card's ID is in
 * the player's unlocked set. Otherwise, the state is "locked".
 *
 * Validates: Requirements 2.2, 2.3
 */
class CardStateRenderingProperties {

    private static final List<String> ALL_HERO_IDS = List.of(
            "bronze-variable", "bronze-string", "bronze-number", "bronze-boolean",
            "bronze-if", "bronze-loop", "bronze-method",
            "silver-class", "silver-object", "silver-constructor",
            "silver-collection", "silver-exception",
            "gold-interface", "gold-generic", "gold-stream",
            "gold-lambda", "gold-optional", "gold-record",
            "spring-controller", "spring-service", "spring-repository",
            "spring-bean", "spring-di"
    );

    /**
     * Determines the display state of a hero card given the player's unlocked set.
     */
    private String getDisplayState(Set<String> unlockedHeroIds, String heroId) {
        return unlockedHeroIds.contains(heroId) ? "completed" : "locked";
    }

    /**
     * **Validates: Requirements 2.2, 2.3**
     *
     * Property: For any player state and any hero card, the display state equals
     * "completed" if and only if the card is in the unlocked set.
     */
    @Property(tries = 100)
    void cardDisplayStateIsCompletedIfAndOnlyIfUnlocked(
            @ForAll("unlockedHeroIdSets") Set<String> unlockedHeroIds,
            @ForAll("heroCardIds") String heroId
    ) {
        String displayState = getDisplayState(unlockedHeroIds, heroId);

        if (unlockedHeroIds.contains(heroId)) {
            assert displayState.equals("completed") :
                    "Card " + heroId + " is in unlocked set but display state is '" + displayState + "', expected 'completed'";
        } else {
            assert displayState.equals("locked") :
                    "Card " + heroId + " is NOT in unlocked set but display state is '" + displayState + "', expected 'locked'";
        }
    }

    /**
     * **Validates: Requirements 2.2, 2.3**
     *
     * Property: The display state is always one of exactly two values: "completed" or "locked".
     * No other state is possible.
     */
    @Property(tries = 100)
    void cardDisplayStateIsAlwaysCompletedOrLocked(
            @ForAll("unlockedHeroIdSets") Set<String> unlockedHeroIds,
            @ForAll("heroCardIds") String heroId
    ) {
        String displayState = getDisplayState(unlockedHeroIds, heroId);

        assert displayState.equals("completed") || displayState.equals("locked") :
                "Display state '" + displayState + "' is neither 'completed' nor 'locked'";
    }

    @Provide
    Arbitrary<Set<String>> unlockedHeroIdSets() {
        return Arbitraries.of(ALL_HERO_IDS)
                .set()
                .ofMinSize(0)
                .ofMaxSize(ALL_HERO_IDS.size());
    }

    @Provide
    Arbitrary<String> heroCardIds() {
        return Arbitraries.of(ALL_HERO_IDS);
    }
}
