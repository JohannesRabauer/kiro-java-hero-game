package com.javahero.model;

public enum LearningPath {
    BRONZE(1, 10),
    SILVER(2, 20),
    GOLD(3, 30),
    SPRING_MASTER(4, 50);

    private final int order;
    private final int xpPerCard;

    LearningPath(int order, int xpPerCard) {
        this.order = order;
        this.xpPerCard = xpPerCard;
    }

    public int getOrder() {
        return order;
    }

    public int getXpPerCard() {
        return xpPerCard;
    }
}
