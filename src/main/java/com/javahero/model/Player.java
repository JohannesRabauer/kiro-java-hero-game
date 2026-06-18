package com.javahero.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private int totalExperiencePoints;

    private boolean onboardingCompleted;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"current_path\"", columnDefinition = "varchar(255)")
    private LearningPath currentPath;

    @ElementCollection
    private Set<String> unlockedHeroIds = new HashSet<>();

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;

    public Player() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
