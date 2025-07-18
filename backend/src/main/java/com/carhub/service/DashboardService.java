package com.carhub.service;

import com.carhub.dto.DashboardStats;
import com.carhub.entity.Car;
import com.carhub.entity.Sale;
import com.carhub.repository.CarRepository;
import com.carhub.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Month;

@Service
public class DashboardService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private SaleRepository saleRepository;

    public DashboardStats getDashboardStats() {
        long totalCars = carRepository.count();
        long availableCars = carRepository.countByStatus(Car.Status.AVAILABLE);
        long soldCars = carRepository.countByStatus(Car.Status.SOLD);
        
        // Cars sold this month
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        long carsSoldThisMonth = saleRepository.getSalesCount(startOfMonth, endOfMonth);
        
        // Total revenue (last 12 months for general stats)
        BigDecimal totalRevenue = saleRepository.getTotalRevenue(
            LocalDateTime.now().minusYears(1), LocalDateTime.now());
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        
        // Average sale price
        BigDecimal averageSalePrice = carRepository.getAverageSalePrice();
        if (averageSalePrice == null) averageSalePrice = BigDecimal.ZERO;
        
        long pendingSales = 0; // Implement if needed

        return new DashboardStats(totalCars, availableCars, soldCars, carsSoldThisMonth, 
                                 totalRevenue, averageSalePrice, pendingSales);
    }

    public Map<String, Object> getChartData(int year) {
        Map<String, Object> chartData = new HashMap<>();
        
        // Monthly sales data for the specified year
        List<Object[]> monthlySales = saleRepository.getMonthlySales(year);
        chartData.put("monthlySales", monthlySales);
        
        // Sales by brand
        List<Object[]> salesByBrand = carRepository.getSalesByBrand();
        chartData.put("salesByBrand", salesByBrand);
        
        // Recent activities
        List<Sale> recentSales = saleRepository.findTop10ByOrderBySaleDateDesc();
        chartData.put("recentSales", recentSales);

        // Monthly revenue for the last 6 months
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(6).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<Object[]> last6MonthsRevenueRaw = saleRepository.getMonthlyRevenueForPeriod(startDate, endDate);
        
        // Prepare data for the last 6 months, filling in zero for months with no sales
        Map<Integer, BigDecimal> monthlyRevenueMap = new HashMap<>();
        for (Object[] row : last6MonthsRevenueRaw) {
            // DATE_TRUNC returns a timestamp, extract month
            LocalDateTime monthStart = (LocalDateTime) row[0];
            BigDecimal revenue = (BigDecimal) row[1];
            monthlyRevenueMap.put(monthStart.getMonthValue(), revenue);
        }

        List<String> monthLabels = new ArrayList<>();
        List<BigDecimal> monthlyRevenueData = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            monthLabels.add(ym.getMonth().name().substring(0, 3)); // e.g., JAN, FEB
            monthlyRevenueData.add(monthlyRevenueMap.getOrDefault(ym.getMonthValue(), BigDecimal.ZERO));
        }
        chartData.put("last6MonthsLabels", monthLabels);
        chartData.put("last6MonthsRevenue", monthlyRevenueData);
        
        return chartData;
    }
}
