package com.carcrash.web;

import com.carcrash.model.Car;
import com.carcrash.service.SimulationService;
import com.carcrash.web.dto.AddCarRequest;
import com.carcrash.web.dto.CarResponse;
import com.carcrash.web.dto.CreateFieldRequest;
import com.carcrash.web.dto.FieldResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/field")
    public ResponseEntity<FieldResponse> createField(@RequestBody CreateFieldRequest request) {
        var field = simulationService.createField(request.width(), request.height());
        return ResponseEntity.status(HttpStatus.CREATED).body(FieldResponse.from(field));
    }

    @PostMapping("/cars")
    public ResponseEntity<CarResponse> addCar(@RequestBody AddCarRequest request) {
        Car car = simulationService.addCar(
                request.name(), request.x(), request.y(), request.direction(), request.commands());
        return ResponseEntity.status(HttpStatus.CREATED).body(CarResponse.from(car));
    }

    @GetMapping("/cars")
    public List<CarResponse> listCars() {
        return simulationService.getCars().stream().map(CarResponse::from).toList();
    }

    @PostMapping("/simulate")
    public List<CarResponse> simulate() {
        return simulationService.runSimulation().stream().map(CarResponse::from).toList();
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> reset() {
        simulationService.reset();
        return ResponseEntity.noContent().build();
    }
}
