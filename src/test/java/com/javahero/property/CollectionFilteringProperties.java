package com.javahero.property;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Property 11: Collection filtering correctness
 *
 * For any player collection (set of unlocked hero IDs) and for any selected path filter,
 * all hero cards returned by the filter belong to the selected learning path.
 * When the filter is "All", every unlocked card is returned.
 *
 * Validates: Requirements 7.4
 */
class CollectionFilteringProperties {

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

    private static final Map<String, List<String>> PATH_CARDS = Map.of(
            "BRONZE", List.of("bronze-variable", "bronze-string", "bronze-number",
                    "bronze-boolean", "bronze-if", "bronze-loop", "bronze-method"),
            "SILVER", List.of("silver-class", "silver-object", "silver-constructor",
                    "silver-collection", "silver-exception"),
            "GOLD", List.of("gold-interface", "gold-generic", "gold-stream",
                    "gold-lambda", "gold-optional", "gold-record"),
            "SPRING_MASTER", List.of("spring-controller", "spring-service",
                    "spring-repository", "spring-bean", "spring-di")
    );

    private static final List<String> FILTER_OPTIONS = List.of(
            "BRONZE", "SILVER", "GOLD", "SPRING_MASTER", "ALL"
    );

    /**
     * Applies a collection filter to the player's unlocked cards.
     * If filter is "ALL", returns all unlocked cards.
     * Otherwise, returns only the unlocked cards that belong to the selected path.
     */
    private Set<String> applyFilter(Set<String> unlockedHeroIds, String filter) {
        if ("ALL".equals(filter)) {
            return new HashSet<>(unlockedHeroIds);
        }
        List<String> pathCards = PATH_CARDS.get(filter);
        if (pathCards == null) {
            return Set.of();
        }
        return unlockedHeroIds.stream()
                .filter(pathCards::contains)
                .collect(Collectors.toSet());
    }

    /**
     * Returns the path a hero card belongs to, or null if not found.
     */
    private String getPathForCard(String heroId) {
        for (Map.Entry<String, List<String>> entry : PATH_CARDS.entrySet()) {
            if (entry.getValue().contains(heroId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * **Validates: Requirements 7.4**
     *
     * Property: For any player collection and any selected path filter (not "ALL"),
     * all hero cards returned by the filter belong to the selected learning path.
     */
    @Property(tries = 100)
    void filteredCardsAllBelongToSelectedPath(
            @ForAll("unlockedHeroIdSets") Set<String> unlockedHeroIds,
            @ForAll("pathFilters") String pathFilter
    ) {
        Set<String> filteredCards = applyFilter(unlockedHeroIds, pathFilter);

        for (String cardId : filteredCards) {
            String cardPath = getPathForCard(cardId);
            assert pathFilter.equals(cardPath) :
                    "Card '" + cardId + "' belongs to path '" + cardPath
                            + "' but was returned by filter '" + pathFilter + "'";
        }
    }

    /**
     * **Validates: Requirements 7.4**
     *
     * Property: When the filter is "All", every unlocked card is returned.
     */
    @Property(tries = 100)
    void allFilterReturnsEveryUnlockedCard(
            @ForAll("unlockedHeroIdSets") Set<String> unlockedHeroIds
    ) {
        Set<String> filteredCards = applyFilter(unlockedHeroIds, "ALL");

        assert filteredCards.equals(unlockedHeroIds) :
                "ALL filter returned " + filteredCards.size() + " cards but player has "
                        + unlockedHeroIds.size() + " unlocked cards. Missing: "
                        + unlockedHeroIds.stream()
                        .filter(id -> !filteredCards.contains(id))
                        .collect(Collectors.joining(", "));
    }

    @Provide
    Arbitrary<Set<String>> unlockedHeroIdSets() {
        return Arbitraries.of(ALL_HERO_IDS)
                .set()
                .ofMinSize(0)
                .ofMaxSize(ALL_HERO_IDS.size());
    }

    @Provide
    Arbitrary<String> pathFilters() {
        return Arbitraries.of("BRONZE", "SILVER", "GOLD", "SPRING_MASTER");
    }
}
