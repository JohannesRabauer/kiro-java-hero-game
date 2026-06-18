package com.javahero.dto;

import com.javahero.model.LearningPath;

public class PathProgress {

    private LearningPath path;
    private int totalCards;
    private int unlockedCards;
    private boolean completed;

    public PathProgress() {
    }

    public PathProgress(LearningPath path, int totalCards, int unlockedCards, boolean completed) {
        this.path = path;
        this.totalCards = totalCards;
        this.unlockedCards = unlockedCards;
        this.completed = completed;
    }

    public LearningPath getPath() {
        return path;
    }

    public void setPath(LearningPath path) {
        this.path = path;
    }

    public int getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(int totalCards) {
        this.totalCards = totalCards;
    }

    public int getUnlockedCards() {
        return unlockedCards;
    }

    public void setUnlockedCards(int unlockedCards) {
        this.unlockedCards = unlockedCards;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
