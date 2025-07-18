package com.carhub.service;

import com.carhub.dto.SaleDTO;
import com.carhub.entity.Admin;
import com.carhub.entity.Car;
import com.carhub.entity.Client;
import com.carhub.entity.Sale;
import com.carhub.repository.AdminRepository;
import com.carhub.repository.CarRepository;
import com.carhub.repository.ClientRepository;
import com.carhub.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AdminRepository adminRepository;

    public Page<Sale> getAllSales(Pageable pageable) {
        return saleRepository.findAll(pageable);
    }

    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found with id: " + id));
    }

    public Sale createSale(SaleDTO saleDTO) {
        Car car = carRepository.findById(saleDTO.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + saleDTO.getCarId()));
        Client client = clientRepository.findById(saleDTO.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + saleDTO.getClientId()));
        Admin admin = adminRepository.findById(saleDTO.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + saleDTO.getAdminId()));

        Sale sale = new Sale();
        sale.setCar(car);
        sale.setClient(client);
        sale.setAdmin(admin);
        sale.setSalePrice(saleDTO.getSalePrice());
        sale.setPaymentMethod(saleDTO.getPaymentMethod());
        sale.setNotes(saleDTO.getNotes());
        sale.setStatus(saleDTO.getStatus() != null ? saleDTO.getStatus() : Sale.Status.COMPLETED);
        if (saleDTO.getSaleDate() != null) {
            sale.setSaleDate(saleDTO.getSaleDate());
        }
        
        // Update car status to SOLD
        car.setStatus(Car.Status.SOLD);
        carRepository.save(car);

        return saleRepository.save(sale);
    }

    public Sale updateSale(Long id, SaleDTO saleDTO) {
        Sale sale = getSaleById(id);
        Car car = carRepository.findById(saleDTO.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + saleDTO.getCarId()));
        Client client = clientRepository.findById(saleDTO.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + saleDTO.getClientId()));
        Admin admin = adminRepository.findById(saleDTO.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + saleDTO.getAdminId()));

        sale.setCar(car);
        sale.setClient(client);
        sale.setAdmin(admin);
        sale.setSalePrice(saleDTO.getSalePrice());
        sale.setPaymentMethod(saleDTO.getPaymentMethod());
        sale.setNotes(saleDTO.getNotes());
        sale.setStatus(saleDTO.getStatus() != null ? saleDTO.getStatus() : Sale.Status.COMPLETED);
        if (saleDTO.getSaleDate() != null) {
            sale.setSaleDate(saleDTO.getSaleDate());
        }

        return saleRepository.save(sale);
    }

    public void deleteSale(Long id) {
        Sale sale = getSaleById(id);
        // Optionally revert car status if needed
        // Car car = sale.getCar();
        // car.setStatus(Car.Status.AVAILABLE);
        // carRepository.save(car);
        saleRepository.delete(sale);
    }

    public Page<Sale> getSalesBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return saleRepository.findBySaleDateBetween(startDate, endDate, pageable);
    }
}
