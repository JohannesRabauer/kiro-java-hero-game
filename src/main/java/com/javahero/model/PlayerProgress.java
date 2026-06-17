package com.javahero.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@SessionScope
public class PlayerProgress {

    private Set<String> unlockedHeroIds = new HashSet<>();
    private int xp = 0;

    public boolean isUnlocked(String heroId) {
        return unlockedHeroIds.contains(heroId);
    }

    public void unlock(String heroId) {
        unlockedHeroIds.add(heroId);
    }

    public void addXp(int amount) {
        this.xp += amount;
    }

    public int getXp() {
        return xp;
    }

    public Set<String> getUnlockedHeroIds() {
        return unlockedHeroIds;
    }

    public void setUnlockedHeroIds(Set<String> unlockedHeroIds) {
        this.unlockedHeroIds = unlockedHeroIds;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    /**
     * Returns the ID of the next hero to learn (first locked hero in order).
     */
    public String getNextHeroId(List<Hero> allHeroesSorted) {
        for (Hero hero : allHeroesSorted) {
            if (!isUnlocked(hero.getId())) {
                return hero.getId();
            }
        }
        return null;
    }

    public int getUnlockedCount() {
        return unlockedHeroIds.size();
    }
}
