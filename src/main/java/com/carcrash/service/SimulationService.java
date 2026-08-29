package com.carcrash.service;

import com.carcrash.model.Car;
import com.carcrash.model.Direction;
import com.carcrash.model.Field;
import com.carcrash.model.Position;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class SimulationService {

    private final SimulationEngine engine = new SimulationEngine();

    private Field field;
    private final Map<String, Car> cars = new LinkedHashMap<>();
    private boolean simulationRun = false;

    public synchronized Field createField(int width, int height) {
        this.field = new Field(width, height);
        this.cars.clear();
        this.simulationRun = false;
        return this.field;
    }

    public synchronized Car addCar(String name, int x, int y, String directionSymbol, String commands) {
        if (field == null) {
            throw new IllegalStateException("Create a field before adding cars");
        }
        if (cars.containsKey(name)) {
            throw new IllegalArgumentException("A car named '" + name + "' already exists");
        }
        Position start = new Position(x, y);
        if (!field.contains(start)) {
            throw new IllegalArgumentException(
                    "Starting position " + start + " is outside the " + field.width() + "x" + field.height() + " field");
        }
        for (Car existing : cars.values()) {
            if (existing.getStartPosition().equals(start)) {
                throw new IllegalArgumentException(
                        "Car '" + name + "' cannot start at " + start + " — car '" + existing.getName()
                                + "' already starts there");
            }
        }
        Direction direction = Direction.fromSymbol(directionSymbol);
        Car car = new Car(name, start, direction, commands);
        cars.put(car.getName(), car);
        simulationRun = false;
        return car;
    }

    public synchronized List<Car> getCars() {
        return new ArrayList<>(cars.values());
    }

    public synchronized Field getField() {
        return field;
    }

    public synchronized List<Car> runSimulation() {
        if (field == null) {
            throw new IllegalStateException("Create a field before running the simulation");
        }
        if (cars.isEmpty()) {
            throw new IllegalStateException("Add at least one car before running the simulation");
        }
        engine.simulate(field, new ArrayList<>(cars.values()));
        simulationRun = true;
        return getCars();
    }

    public synchronized boolean hasRunSimulation() {
        return simulationRun;
    }

    /** "Start over" — wipes the field and every car, back to a blank slate. */
    public synchronized void reset() {
        this.field = null;
        this.cars.clear();
        this.simulationRun = false;
    }

    public synchronized List<Car> unmodifiableCarsView() {
        return Collections.unmodifiableList(getCars());
    }
}
