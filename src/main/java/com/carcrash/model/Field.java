package com.carcrash.model;

/**
 * A rectangular field. Bottom-left is (0,0). A field of width W and height H
 * has valid coordinates 0..W-1 and 0..H-1 (so a 10x10 field's top-right
 * corner is (9,9), matching the problem statement).
 */
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
