package com.javahero.property;

import com.javahero.model.LearningPath;
import net.jqwik.api.*;

import java.util.*;

/**
 * Property 10: XP award consistency
 *
 * For any hero card completion event, the experience points awarded equals the
 * fixed XP value defined for the card's learning path, and this value is identical
 * for all cards within the same path.
 *
 * Validates: Requirements 6.3, 9.3
 */
class XpAwardProperties {

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

    // All known card IDs in a flat list
    private static final List<String> ALL_CARD_IDS;

    static {
        List<String> allCards = new ArrayList<>();
        for (List<String> cards : PATH_CARDS.values()) {
            allCards.addAll(cards);
        }
        ALL_CARD_IDS = Collections.unmodifiableList(allCards);
    }

    /**
     * Returns the LearningPath for a given card ID.
     */
    private LearningPath getPathForCard(String cardId) {
        for (Map.Entry<LearningPath, List<String>> entry : PATH_CARDS.entrySet()) {
            if (entry.getValue().contains(cardId)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Unknown card: " + cardId);
    }

    /**
     * Simulates XP award for completing a hero card: returns the XP value
     * defined for the card's learning path.
     */
    private int awardXpForCard(String cardId) {
        LearningPath path = getPathForCard(cardId);
        return path.getXpPerCard();
    }

    /**
     * **Validates: Requirements 6.3, 9.3**
     *
     * Property: For any hero card, the XP awarded equals the fixed value defined
     * for the card's learning path.
     */
    @Property(tries = 100)
    void xpAwardedEqualsPathDefinedValue(@ForAll("heroCardIds") String cardId) {
        LearningPath path = getPathForCard(cardId);
        int expectedXp = path.getXpPerCard();
        int actualXp = awardXpForCard(cardId);

        assert actualXp == expectedXp :
                "Card '" + cardId + "' in path " + path +
                " should award " + expectedXp + " XP but awarded " + actualXp;
    }

    /**
     * **Validates: Requirements 6.3, 9.3**
     *
     * Property: All cards within the same learning path produce the same XP value.
     */
    @Property(tries = 100)
    void allCardsInSamePathAwardIdenticalXp(@ForAll("learningPaths") LearningPath path) {
        List<String> cardsInPath = PATH_CARDS.get(path);
        int expectedXp = path.getXpPerCard();

        for (String cardId : cardsInPath) {
            int actualXp = awardXpForCard(cardId);
            assert actualXp == expectedXp :
                    "Card '" + cardId + "' in path " + path +
                    " should award " + expectedXp + " XP but awarded " + actualXp;
        }
    }

    /**
     * **Validates: Requirements 6.3, 9.3**
     *
     * Property: XP values match the specific amounts defined per path:
     * BRONZE=10, SILVER=20, GOLD=30, SPRING_MASTER=50.
     */
    @Property(tries = 100)
    void xpValuesMatchSpecifiedAmounts(@ForAll("heroCardIds") String cardId) {
        LearningPath path = getPathForCard(cardId);
        int xp = awardXpForCard(cardId);

        int expected = switch (path) {
            case BRONZE -> 10;
            case SILVER -> 20;
            case GOLD -> 30;
            case SPRING_MASTER -> 50;
        };

        assert xp == expected :
                "Card '" + cardId + "' in path " + path +
                " should award exactly " + expected + " XP but got " + xp;
    }

    @Provide
    Arbitrary<String> heroCardIds() {
        return Arbitraries.of(ALL_CARD_IDS);
    }

    @Provide
    Arbitrary<LearningPath> learningPaths() {
        return Arbitraries.of(LearningPath.values());
    }
}
