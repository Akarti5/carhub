package com.carhub.controller;

import com.carhub.entity.Car;
import com.carhub.entity.Sale;
import com.carhub.service.CarService;
import com.carhub.service.PdfService;
import com.carhub.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private CarService carService;

    @Autowired
    private SaleRepository saleRepository;

    @GetMapping("/cars/inventory/pdf")
    public ResponseEntity<byte[]> generateCarInventoryPdf(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Car.Status status) {
        
        try {
            // Get all cars with filters
           List<Car> cars = carService.getCarsWithFilters(
    brand,
    model,
    null, // minPrice
    null, // maxPrice
    null, // year
    status,
    null, // createdAfter
    null, // createdBefore
    PageRequest.of(0, 1000) // pageable
).getContent();

            byte[] pdfBytes = pdfService.generateCarInventoryReport(cars);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "car-inventory-report.pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sales/invoice/{saleId}/pdf")
    public ResponseEntity<byte[]> generateSaleInvoicePdf(@PathVariable Long saleId) {
        try {
            Sale sale = saleRepository.findById(saleId)
                    .orElseThrow(() -> new RuntimeException("Sale not found"));

            byte[] pdfBytes = pdfService.generateSaleInvoice(sale);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "invoice-" + sale.getInvoiceNumber() + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sales/monthly/pdf")
    public ResponseEntity<byte[]> generateMonthlySalesReportPdf(
            @RequestParam(defaultValue = "2024") int year,
            @RequestParam(defaultValue = "1") int month) {
        
        try {
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

            List<Sale> sales = saleRepository.findByOrderBySaleDateDesc(PageRequest.of(0, 1000))
                    .getContent()
                    .stream()
                    .filter(sale -> sale.getSaleDate().isAfter(startOfMonth) && 
                                   sale.getSaleDate().isBefore(endOfMonth))
                    .toList();

            byte[] pdfBytes = pdfService.generateMonthlySalesReport(sales, 
                yearMonth.getMonth().toString(), String.valueOf(year));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "monthly-sales-report-" + year + "-" + month + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
