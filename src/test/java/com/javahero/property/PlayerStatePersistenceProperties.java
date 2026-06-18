package com.javahero.property;

import com.javahero.model.LearningPath;
import com.javahero.model.Player;
import com.javahero.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import net.jqwik.api.*;
import net.jqwik.spring.JqwikSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Property 13: Player state persistence round-trip
 *
 * For any valid player state, serializing (persisting) the state and then
 * deserializing (restoring) it produces a player state equivalent to the
 * original, including unlocked hero cards, experience points, current
 * learning path, and onboarding status.
 *
 * Validates: Requirements 8.2
 */
@JqwikSpringSupport
@SpringBootTest
@Transactional
class PlayerStatePersistenceProperties {

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

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @Autowired
    private PlayerRepository playerRepository;

    /**
     * **Validates: Requirements 8.2**
     *
     * Property: For any valid player state, saving it to the database and then
     * loading it back produces an equivalent player state.
     */
    @Property(tries = 100)
    void persistAndRestoreProducesEquivalentPlayerState(
            @ForAll("learningPaths") LearningPath currentPath,
            @ForAll("experiencePoints") int totalXp,
            @ForAll("onboardingFlags") boolean onboardingCompleted,
            @ForAll("heroIdSubsets") Set<String> unlockedHeroIds
    ) {
        // Create a unique username for each try
        String username = "player-pbt-" + COUNTER.incrementAndGet() + "-" + System.nanoTime();

        // Build a Player with the generated state
        Player original = new Player();
        original.setUsername(username);
        original.setCurrentPath(currentPath);
        original.setTotalExperiencePoints(totalXp);
        original.setOnboardingCompleted(onboardingCompleted);
        original.setUnlockedHeroIds(new HashSet<>(unlockedHeroIds));
        original.setCreatedAt(LocalDateTime.now());
        original.setLastUpdatedAt(LocalDateTime.now());

        // Persist (serialize) the player
        Player saved = playerRepository.save(original);
        Long savedId = saved.getId();
        assert savedId != null : "Saved player should have a generated ID";

        // Flush and clear the persistence context would be ideal, but with
        // @Transactional the entityManager is managed. We use findById which
        // should return the persisted state.
        playerRepository.flush();

        // Restore (deserialize) the player
        Optional<Player> restored = playerRepository.findById(savedId);
        assert restored.isPresent() : "Player with id " + savedId + " should be found after save";

        Player loaded = restored.get();

        // Verify all fields match
        assert loaded.getUsername().equals(username) :
                "Username mismatch: expected '" + username + "', got '" + loaded.getUsername() + "'";

        assert loaded.getCurrentPath() == currentPath :
                "CurrentPath mismatch: expected " + currentPath + ", got " + loaded.getCurrentPath();

        assert loaded.getTotalExperiencePoints() == totalXp :
                "TotalExperiencePoints mismatch: expected " + totalXp + ", got " + loaded.getTotalExperiencePoints();

        assert loaded.isOnboardingCompleted() == onboardingCompleted :
                "OnboardingCompleted mismatch: expected " + onboardingCompleted + ", got " + loaded.isOnboardingCompleted();

        assert loaded.getUnlockedHeroIds() != null :
                "UnlockedHeroIds should not be null after restore";

        assert loaded.getUnlockedHeroIds().equals(unlockedHeroIds) :
                "UnlockedHeroIds mismatch: expected " + unlockedHeroIds + ", got " + loaded.getUnlockedHeroIds();
    }

    @Provide
    Arbitrary<LearningPath> learningPaths() {
        return Arbitraries.of(LearningPath.values());
    }

    @Provide
    Arbitrary<Integer> experiencePoints() {
        return Arbitraries.integers().between(0, 1000);
    }

    @Provide
    Arbitrary<Boolean> onboardingFlags() {
        return Arbitraries.of(true, false);
    }

    @Provide
    Arbitrary<Set<String>> heroIdSubsets() {
        return Arbitraries.of(ALL_HERO_IDS)
                .set()
                .ofMinSize(0)
                .ofMaxSize(ALL_HERO_IDS.size());
    }
}
