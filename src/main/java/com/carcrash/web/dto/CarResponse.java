package com.carcrash.web.dto;

import com.carcrash.model.Car;

import java.util.List;

public record CarResponse(
        String name,
        int x,
        int y,
        String direction,
        String commands,
        boolean collided,
        int collisionStep,
        List<String> collidedWith
) {
    public static CarResponse from(Car car) {
        return new CarResponse(
                car.getName(),
                car.getPosition().x(),
                car.getPosition().y(),
                car.getDirection().name(),
                car.getCommands(),
                car.isCollided(),
                car.getCollisionStep(),
                car.getCollidedWith()
        );
    }
}
