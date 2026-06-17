package com.javahero.model;

public enum LearningPath {
    BRONZE("Bronze", "#cd7f32", 1),
    SILVER("Silver", "#c0c0c0", 2),
    GOLD("Gold", "#ffd700", 3),
    SPRING_MASTER("Spring Master", "#6db33f", 4);

    private final String displayName;
    private final String color;
    private final int order;

    LearningPath(String displayName, String color, int order) {
        this.displayName = displayName;
        this.color = color;
        this.order = order;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }

    public int getOrder() {
        return order;
    }

    public String getCssClass() {
        return name().toLowerCase().replace("_", "-");
    }
}
