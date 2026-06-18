package com.javahero.service;

import com.javahero.exception.PersistenceFailedException;
import com.javahero.model.LearningPath;
import com.javahero.model.Player;
import com.javahero.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

@Service
public class PlayerService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 100;

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Save player to database with retry logic.
     * Tries up to 3 times with exponential backoff (100ms, 200ms, 400ms).
     * If all retries fail, throws PersistenceFailedException.
     */
    public Player savePlayer(Player player) {
        player.setLastUpdatedAt(LocalDateTime.now());

        Exception lastException = null;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return playerRepository.save(player);
            } catch (Exception e) {
                lastException = e;
                logger.warn("Save attempt {} of {} failed for player {}: {}",
                        attempt, MAX_RETRIES, player.getUsername(), e.getMessage());
                if (attempt < MAX_RETRIES) {
                    long backoffMs = INITIAL_BACKOFF_MS * (1L << (attempt - 1));
                    try {
                        Thread.sleep(backoffMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new PersistenceFailedException(
                                "Save interrupted during retry backoff", ie);
                    }
                }
            }
        }
        throw new PersistenceFailedException(
                "Failed to save player after " + MAX_RETRIES + " attempts", lastException);
    }

    /**
     * Load player from PlayerRepository by ID.
     * Throws IllegalArgumentException if not found.
     */
    public Player restorePlayer(Long playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Player not found with id: " + playerId));
    }

    /**
     * Find player by username. If not found, create a new player with initial state:
     * - currentPath = BRONZE
     * - totalExperiencePoints = 0
     * - onboardingCompleted = false
     * - unlockedHeroIds = empty set
     * - createdAt = now
     * - lastUpdatedAt = now
     */
    public Player restoreOrCreatePlayer(String username) {
        return playerRepository.findByUsername(username)
                .orElseGet(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setUsername(username);
                    newPlayer.setCurrentPath(LearningPath.BRONZE);
                    newPlayer.setTotalExperiencePoints(0);
                    newPlayer.setOnboardingCompleted(false);
                    newPlayer.setUnlockedHeroIds(new HashSet<>());
                    newPlayer.setCreatedAt(LocalDateTime.now());
                    newPlayer.setLastUpdatedAt(LocalDateTime.now());
                    return playerRepository.save(newPlayer);
                });
    }

    /**
     * Find player by username, return Optional.
     */
    public Optional<Player> getPlayerByUsername(String username) {
        return playerRepository.findByUsername(username);
    }
}
