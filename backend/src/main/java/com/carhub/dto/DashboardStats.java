package com.carhub.dto;

import java.math.BigDecimal;

public class DashboardStats {
    private long totalCars;
    private long availableCars;
    private long soldCars;
    private long carsSoldThisMonth;
    private BigDecimal totalRevenue;
    private BigDecimal averageSalePrice;
    private long pendingSales;

    // Constructors
    public DashboardStats() {}

    public DashboardStats(long totalCars, long availableCars, long soldCars, 
                         long carsSoldThisMonth, BigDecimal totalRevenue, 
                         BigDecimal averageSalePrice, long pendingSales) {
        this.totalCars = totalCars;
        this.availableCars = availableCars;
        this.soldCars = soldCars;
        this.carsSoldThisMonth = carsSoldThisMonth;
        this.totalRevenue = totalRevenue;
        this.averageSalePrice = averageSalePrice;
        this.pendingSales = pendingSales;
    }

    // Getters and Setters
    public long getTotalCars() { return totalCars; }
    public void setTotalCars(long totalCars) { this.totalCars = totalCars; }

    public long getAvailableCars() { return availableCars; }
    public void setAvailableCars(long availableCars) { this.availableCars = availableCars; }

    public long getSoldCars() { return soldCars; }
    public void setSoldCars(long soldCars) { this.soldCars = soldCars; }

    public long getCarsSoldThisMonth() { return carsSoldThisMonth; }
    public void setCarsSoldThisMonth(long carsSoldThisMonth) { this.carsSoldThisMonth = carsSoldThisMonth; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getAverageSalePrice() { return averageSalePrice; }
    public void setAverageSalePrice(BigDecimal averageSalePrice) { this.averageSalePrice = averageSalePrice; }

    public long getPendingSales() { return pendingSales; }
    public void setPendingSales(long pendingSales) { this.pendingSales = pendingSales; }
}
