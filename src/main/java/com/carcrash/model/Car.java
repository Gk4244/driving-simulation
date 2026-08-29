package com.carcrash.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Car {

    private final String name;
    private final Position startPosition;
    private final Direction startDirection;
    private final String commands;

    private Position position;
    private Direction direction;
    private boolean collided = false;
    private int collisionStep = -1;
    private final List<String> collidedWith = new ArrayList<>();

    public Car(String name, Position startPosition, Direction startDirection, String commands) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Car name must not be empty");
        }
        this.name = name.trim();
        this.startPosition = startPosition;
        this.startDirection = startDirection;
        this.commands = commands == null ? "" : commands.trim().toUpperCase();
        validateCommands(this.commands);

        this.position = startPosition;
        this.direction = startDirection;
    }

    private static void validateCommands(String commands) {
        for (char c : commands.toCharArray()) {
            if (c != 'L' && c != 'R' && c != 'F') {
                throw new IllegalArgumentException(
                        "Invalid command '" + c + "'. Only L, R, F are allowed.");
            }
        }
    }

    public void resetToStart() {
        this.position = startPosition;
        this.direction = startDirection;
        this.collided = false;
        this.collisionStep = -1;
        this.collidedWith.clear();
    }

    public void markCollided(int step, String otherCarName) {
        this.collided = true;
        this.collisionStep = step;
        if (!this.collidedWith.contains(otherCarName)) {
            this.collidedWith.add(otherCarName);
        }
    }

    public String getName() {
        return name;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public Direction getStartDirection() {
        return startDirection;
    }

    public String getCommands() {
        return commands;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isCollided() {
        return collided;
    }

    public int getCollisionStep() {
        return collisionStep;
    }

    public List<String> getCollidedWith() {
        return Collections.unmodifiableList(collidedWith);
    }
}
