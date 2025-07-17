package com.carhub.service;

import com.carhub.dto.CarDTO;
import com.carhub.entity.Car;
import com.carhub.entity.CarImage;
import com.carhub.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    private final String uploadDir = "uploads/cars/";

    public Page<Car> getCarsWithFilters(String brand, String model, BigDecimal minPrice, 
                                       BigDecimal maxPrice, Integer year, Car.Status status, 
                                       Pageable pageable) {
        return carRepository.findCarsWithFilters(brand, model, minPrice, maxPrice, year, status, pageable);
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));
    }

    public Car createCar(CarDTO carDTO) {
        Car car = new Car();
        mapDTOToEntity(carDTO, car);
        return carRepository.save(car);
    }

    public Car updateCar(Long id, CarDTO carDTO) {
        Car car = getCarById(id);
        mapDTOToEntity(carDTO, car);
        return carRepository.save(car);
    }

    public void deleteCar(Long id) {
        Car car = getCarById(id);
        carRepository.delete(car);
    }

    public Car updateCarStatus(Long id, Car.Status status) {
        Car car = getCarById(id);
        car.setStatus(status);
        return carRepository.save(car);
    }

    public Page<Car> getCarsByStatus(Car.Status status, Pageable pageable) {
        return carRepository.findByStatus(status, pageable);
    }

    public void uploadCarImages(Long carId, List<MultipartFile> files) {
        Car car = getCarById(carId);
        
        for (MultipartFile file : files) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, file.getBytes());
                
                CarImage carImage = new CarImage(car, "/uploads/cars/" + fileName, car.getImages().isEmpty());
                car.getImages().add(carImage);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }
        
        carRepository.save(car);
    }

    private void mapDTOToEntity(CarDTO dto, Car car) {
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setYear(dto.getYear());
        car.setPrice(dto.getPrice());
        car.setColor(dto.getColor());
        car.setMileage(dto.getMileage());
        car.setFuelType(dto.getFuelType());
        car.setTransmission(dto.getTransmission());
        car.setDescription(dto.getDescription());
        car.setVin(dto.getVin());
        car.setDoors(dto.getDoors());
        car.setSeats(dto.getSeats());
        car.setEngineSize(dto.getEngineSize());
    }
}
