package com.javahero.service;

import com.javahero.model.Hero;
import com.javahero.model.PlayerProgress;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgressService {

    private static final int XP_PER_HERO = 100;

    /**
     * Unlocks the given hero and awards XP.
     */
    public void completeHero(PlayerProgress progress, String heroId) {
        if (!progress.isUnlocked(heroId)) {
            progress.unlock(heroId);
            progress.addXp(XP_PER_HERO);
        }
    }

    /**
     * Returns completion percentage (0-100).
     */
    public int getCompletionPercentage(PlayerProgress progress, List<Hero> allHeroes) {
        if (allHeroes.isEmpty()) return 0;
        return (progress.getUnlockedCount() * 100) / allHeroes.size();
    }
}
