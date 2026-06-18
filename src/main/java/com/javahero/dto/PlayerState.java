package com.javahero.dto;

import com.javahero.model.LearningPath;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerState {

    private Long playerId;
    private int totalExperiencePoints;
    private boolean onboardingCompleted;
    private LearningPath currentPath;
    private Set<String> unlockedHeroIds = new HashSet<>();
    private String nextAvailableHeroId;
    private Map<LearningPath, PathProgress> pathProgressMap = new HashMap<>();

    public PlayerState() {
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public int getTotalExperiencePoints() {
        return totalExperiencePoints;
    }

    public void setTotalExperiencePoints(int totalExperiencePoints) {
        this.totalExperiencePoints = totalExperiencePoints;
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

    public LearningPath getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(LearningPath currentPath) {
        this.currentPath = currentPath;
    }

    public Set<String> getUnlockedHeroIds() {
        return unlockedHeroIds;
    }

    public void setUnlockedHeroIds(Set<String> unlockedHeroIds) {
        this.unlockedHeroIds = unlockedHeroIds;
    }

    public String getNextAvailableHeroId() {
        return nextAvailableHeroId;
    }

    public void setNextAvailableHeroId(String nextAvailableHeroId) {
        this.nextAvailableHeroId = nextAvailableHeroId;
    }

    public Map<LearningPath, PathProgress> getPathProgressMap() {
        return pathProgressMap;
    }

    public void setPathProgressMap(Map<LearningPath, PathProgress> pathProgressMap) {
        this.pathProgressMap = pathProgressMap;
    }
}
