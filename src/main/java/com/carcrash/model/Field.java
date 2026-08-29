package com.carcrash.model;

public record Field(int width, int height) {

    public Field {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Field width and height must both be positive integers");
        }
    }

    public boolean contains(Position p) {
        return p.x() >= 0 && p.x() < width && p.y() >= 0 && p.y() < height;
    }
}
