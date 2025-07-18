package com.carhub.controller;

import com.carhub.dto.CarDTO;
import com.carhub.entity.Car;
import com.carhub.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "*")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<Car>> getAllCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Car.Status status,
            @RequestParam(required = false) String createdAfter,
            @RequestParam(required = false) String createdBefore) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (createdAfter != null && !createdAfter.isEmpty()) {
            startDate = LocalDate.parse(createdAfter).atStartOfDay();
        }
        if (createdBefore != null && !createdBefore.isEmpty()) {
            endDate = LocalDate.parse(createdBefore).atTime(LocalTime.MAX);
        }

        Page<Car> cars = carService.getCarsWithFilters(brand, model, minPrice, maxPrice, year, status, startDate, endDate, pageable);
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Car car = carService.getCarById(id);
        return ResponseEntity.ok(car);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Car> createCar(@Valid @RequestBody CarDTO carDTO) {
        Car car = carService.createCar(carDTO);
        return ResponseEntity.ok(car);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @Valid @RequestBody CarDTO carDTO) {
        Car car = carService.updateCar(id, carDTO);
        return ResponseEntity.ok(car);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> uploadCarImages(@PathVariable Long id, 
                                           @RequestParam("files") List<MultipartFile> files) {
        carService.uploadCarImages(id, files);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Car> updateCarStatus(@PathVariable Long id, 
                                             @RequestParam Car.Status status) {
        Car car = carService.updateCarStatus(id, status);
        return ResponseEntity.ok(car);
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<Car>> getAvailableCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Car> cars = carService.getCarsByStatus(Car.Status.AVAILABLE, pageable);
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/sold")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<Car>> getSoldCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Car> cars = carService.getCarsByStatus(Car.Status.SOLD, pageable);
        return ResponseEntity.ok(cars);
    }
}
