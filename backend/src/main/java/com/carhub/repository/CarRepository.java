package com.carhub.repository;

import com.carhub.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Page<Car> findByStatus(Car.Status status, Pageable pageable);
    
    @Query("SELECT c FROM Car c WHERE " +
           "(:brand IS NULL OR LOWER(c.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
           "(:model IS NULL OR LOWER(c.model) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
           "(:minPrice IS NULL OR c.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR c.price <= :maxPrice) AND " +
           "(:year IS NULL OR c.year = :year) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:startDate IS NULL OR c.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR c.createdAt <= :endDate)")
    Page<Car> findCarsWithFilters(@Param("brand") String brand,
                                  @Param("model") String model,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  @Param("year") Integer year,
                                  @Param("status") Car.Status status,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate,
                                  Pageable pageable);

    @Query("SELECT COUNT(c) FROM Car c WHERE c.status = :status")
    long countByStatus(@Param("status") Car.Status status);

    @Query("SELECT c.brand, COUNT(c) FROM Car c WHERE c.status = 'SOLD' GROUP BY c.brand")
    List<Object[]> getSalesByBrand();

    @Query("SELECT AVG(c.price) FROM Car c WHERE c.status = 'SOLD'")
    BigDecimal getAverageSalePrice();
}
