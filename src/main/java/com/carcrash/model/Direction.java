package com.carcrash.model;

public enum Direction {
    N, E, S, W;

    private static final Direction[] CLOCKWISE = {N, E, S, W};

    public Direction turnRight() {
        return CLOCKWISE[(this.ordinal() + 1) % 4];
    }

    public Direction turnLeft() {
        return CLOCKWISE[(this.ordinal() + 3) % 4];
    }

    /** Change in x when moving one step forward while facing this direction. */
    public int dx() {
        return switch (this) {
            case E -> 1;
            case W -> -1;
            default -> 0;
        };
    }

    /** Change in y when moving one step forward while facing this direction. */
    public int dy() {
        return switch (this) {
            case N -> 1;
            case S -> -1;
            default -> 0;
        };
    }

    public static Direction fromSymbol(String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Direction must not be empty");
        }
        try {
            return Direction.valueOf(symbol.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid direction '" + symbol + "'. Expected one of N, E, S, W.");
        }
    }
}
