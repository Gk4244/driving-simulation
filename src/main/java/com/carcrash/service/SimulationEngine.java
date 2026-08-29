package com.carcrash.service;

import com.carcrash.model.Car;
import com.carcrash.model.Field;
import com.carcrash.model.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationEngine {

    public void simulate(Field field, List<Car> cars) {
        if (field == null) {
            throw new IllegalStateException("Cannot run a simulation before a field has been created");
        }
        for (Car car : cars) {
            car.resetToStart();
            if (!field.contains(car.getStartPosition())) {
                throw new IllegalStateException(
                        "Car " + car.getName() + " starts outside the field bounds");
            }
        }

        int maxSteps = cars.stream().mapToInt(c -> c.getCommands().length()).max().orElse(0);

        for (int step = 1; step <= maxSteps; step++) {
            for (Car car : cars) {
                if (car.isCollided()) {
                    continue;
                }
                String commands = car.getCommands();
                if (step - 1 >= commands.length()) {
                    continue;
                }
                applyCommand(car, commands.charAt(step - 1), field);
            }
            detectCollisions(cars, step);
        }
    }

    private void applyCommand(Car car, char command, Field field) {
        switch (command) {
            case 'L' -> car.setDirection(car.getDirection().turnLeft());
            case 'R' -> car.setDirection(car.getDirection().turnRight());
            case 'F' -> {
                Position next = car.getPosition().moved(car.getDirection().dx(), car.getDirection().dy());
                if (field.contains(next)) {
                    car.setPosition(next);
                }
                // else: command is ignored, car stays where it is
            }
            default -> throw new IllegalStateException("Unreachable: command already validated as L/R/F");
        }
    }

    private void detectCollisions(List<Car> cars, int step) {
        Map<Position, List<Car>> occupants = new HashMap<>();
        for (Car car : cars) {
            occupants.computeIfAbsent(car.getPosition(), p -> new ArrayList<>()).add(car);
        }
        for (List<Car> group : occupants.values()) {
            if (group.size() < 2) {
                continue;
            }
            for (Car car : group) {
                if (car.isCollided()) {
                    continue; // already recorded at an earlier step
                }
                for (Car other : group) {
                    if (other != car) {
                        car.markCollided(step, other.getName());
                    }
                }
            }
        }
    }
}
