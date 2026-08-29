package com.carcrash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point. State lives entirely in memory (see {@link com.carcrash.service.SimulationService}),
 * so every fresh run of this application starts with an empty field and no cars,
 * satisfying the "clean start every run" requirement.
 */
@SpringBootApplication
public class CarCrashApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarCrashApplication.class, args);
    }
}
