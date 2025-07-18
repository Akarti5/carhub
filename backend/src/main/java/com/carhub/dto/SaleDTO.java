package com.carhub.dto;

import com.carhub.entity.Sale;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaleDTO {
    private Long id; // For updates
    @NotNull
    private Long carId;
    @NotNull
    private Long clientId;
    @NotNull
    private Long adminId;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal salePrice;
    private Sale.PaymentMethod paymentMethod;
    private String notes;
    private Sale.Status status;
    private LocalDateTime saleDate; // For setting custom sale date

    // Constructors
    public SaleDTO() {}

    public SaleDTO(Long id, Long carId, Long clientId, Long adminId, BigDecimal salePrice, Sale.PaymentMethod paymentMethod, String notes, Sale.Status status, LocalDateTime saleDate) {
        this.id = id;
        this.carId = carId;
        this.clientId = clientId;
        this.adminId = adminId;
        this.salePrice = salePrice;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
        this.status = status;
        this.saleDate = saleDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }
    public Sale.PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(Sale.PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Sale.Status getStatus() { return status; }
    public void setStatus(Sale.Status status) { this.status = status; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }
}
