package com.carhub.repository;

import com.carhub.entity.Sale;
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
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Page<Sale> findByOrderBySaleDateDesc(Pageable pageable);
    
    @Query("SELECT SUM(s.salePrice) FROM Sale s WHERE s.saleDate >= :startDate AND s.saleDate <= :endDate")
    BigDecimal getTotalRevenue(@Param("startDate") LocalDateTime startDate, 
                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate >= :startDate AND s.saleDate <= :endDate")
    long getSalesCount(@Param("startDate") LocalDateTime startDate, 
                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT MONTH(s.saleDate), SUM(s.salePrice) FROM Sale s " +
           "WHERE YEAR(s.saleDate) = :year GROUP BY MONTH(s.saleDate) ORDER BY MONTH(s.saleDate)")
    List<Object[]> getMonthlySales(@Param("year") int year);

    List<Sale> findTop10ByOrderBySaleDateDesc();
}
