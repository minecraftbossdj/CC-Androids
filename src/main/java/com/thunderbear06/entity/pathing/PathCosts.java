package com.thunderbear06.entity.pathing;

public enum PathCosts {
    OPEN(1),
    WATER(2),
    NO_SUPPORT(2),
    DANGER(10);

    public final int cost;

    PathCosts(int cost) {
        this.cost = cost;
    }
}
