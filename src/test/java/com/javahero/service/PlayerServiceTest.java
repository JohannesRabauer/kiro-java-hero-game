package com.javahero.service;

import com.javahero.exception.PersistenceFailedException;
import com.javahero.model.LearningPath;
import com.javahero.model.Player;
import com.javahero.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        playerService = new PlayerService(playerRepository);
    }

    @Test
    void savePlayer_succeeds_onFirstAttempt() {
        Player player = createTestPlayer();
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        Player result = playerService.savePlayer(player);

        assertNotNull(result);
        assertNotNull(player.getLastUpdatedAt());
        verify(playerRepository, times(1)).save(player);
    }

    @Test
    void savePlayer_succeeds_onSecondAttempt() {
        Player player = createTestPlayer();
        when(playerRepository.save(any(Player.class)))
                .thenThrow(new RuntimeException("DB error"))
                .thenReturn(player);

        Player result = playerService.savePlayer(player);

        assertNotNull(result);
        verify(playerRepository, times(2)).save(player);
    }

    @Test
    void savePlayer_succeeds_onThirdAttempt() {
        Player player = createTestPlayer();
        when(playerRepository.save(any(Player.class)))
                .thenThrow(new RuntimeException("DB error"))
                .thenThrow(new RuntimeException("DB error again"))
                .thenReturn(player);

        Player result = playerService.savePlayer(player);

        assertNotNull(result);
        verify(playerRepository, times(3)).save(player);
    }

    @Test
    void savePlayer_throwsPersistenceFailedException_afterAllRetriesExhausted() {
        Player player = createTestPlayer();
        when(playerRepository.save(any(Player.class)))
                .thenThrow(new RuntimeException("DB error"));

        PersistenceFailedException exception = assertThrows(
                PersistenceFailedException.class,
                () -> playerService.savePlayer(player));

        assertTrue(exception.getMessage().contains("Failed to save player after 3 attempts"));
        assertNotNull(exception.getCause());
        verify(playerRepository, times(3)).save(player);
    }

    @Test
    void savePlayer_updatesLastUpdatedAt() {
        Player player = createTestPlayer();
        LocalDateTime before = LocalDateTime.now();
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        playerService.savePlayer(player);

        assertNotNull(player.getLastUpdatedAt());
        assertTrue(player.getLastUpdatedAt().isAfter(before.minusSeconds(1)));
    }

    @Test
    void restorePlayer_returnsPlayer_whenFound() {
        Player player = createTestPlayer();
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Player result = playerService.restorePlayer(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void restorePlayer_throwsException_whenNotFound() {
        when(playerRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> playerService.restorePlayer(99L));

        assertTrue(exception.getMessage().contains("Player not found with id: 99"));
    }

    @Test
    void restoreOrCreatePlayer_returnsExistingPlayer() {
        Player existing = createTestPlayer();
        when(playerRepository.findByUsername("testuser")).thenReturn(Optional.of(existing));

        Player result = playerService.restoreOrCreatePlayer("testuser");

        assertEquals("testuser", result.getUsername());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void restoreOrCreatePlayer_createsNewPlayer_whenNotFound() {
        when(playerRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Player result = playerService.restoreOrCreatePlayer("newuser");

        assertEquals("newuser", result.getUsername());
        assertEquals(LearningPath.BRONZE, result.getCurrentPath());
        assertEquals(0, result.getTotalExperiencePoints());
        assertFalse(result.isOnboardingCompleted());
        assertTrue(result.getUnlockedHeroIds().isEmpty());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getLastUpdatedAt());
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void getPlayerByUsername_returnsOptionalWithPlayer() {
        Player player = createTestPlayer();
        when(playerRepository.findByUsername("testuser")).thenReturn(Optional.of(player));

        Optional<Player> result = playerService.getPlayerByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void getPlayerByUsername_returnsEmptyOptional_whenNotFound() {
        when(playerRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<Player> result = playerService.getPlayerByUsername("unknown");

        assertTrue(result.isEmpty());
    }

    private Player createTestPlayer() {
        Player player = new Player();
        player.setId(1L);
        player.setUsername("testuser");
        player.setCurrentPath(LearningPath.BRONZE);
        player.setTotalExperiencePoints(0);
        player.setOnboardingCompleted(false);
        player.setUnlockedHeroIds(new HashSet<>(Set.of("variable")));
        player.setCreatedAt(LocalDateTime.now());
        player.setLastUpdatedAt(LocalDateTime.now());
        return player;
    }
}
