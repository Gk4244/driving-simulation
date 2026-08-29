package com.carcrash.model;

public record Position(int x, int y) {

    public Position moved(int dx, int dy) {
        return new Position(this.x + dx, this.y + dy);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
