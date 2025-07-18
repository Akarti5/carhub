package com.carhub.controller;

import com.carhub.dto.SaleDTO;
import com.carhub.entity.Client;
import com.carhub.entity.Sale;
import com.carhub.service.EmailService;
import com.carhub.service.PdfService;
import com.carhub.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SaleController {

    @Autowired
    private SaleService saleService;
    @Autowired
    private PdfService pdfService;
    @Autowired
    private EmailService emailService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<Sale>> getAllSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "saleDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Sale> sales;
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(LocalTime.MAX);
            sales = saleService.getSalesBetweenDates(startDateTime, endDateTime, pageable);
        } else {
            sales = saleService.getAllSales(pageable);
        }
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        Sale sale = saleService.getSaleById(id);
        return ResponseEntity.ok(sale);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Sale> createSale(@Valid @RequestBody SaleDTO saleDTO) {
        Sale sale = saleService.createSale(saleDTO);
        return ResponseEntity.ok(sale);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Sale> updateSale(@PathVariable Long id, @Valid @RequestBody SaleDTO saleDTO) {
        Sale sale = saleService.updateSale(id, saleDTO);
        return ResponseEntity.ok(sale);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{saleId}/send-invoice-email")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> sendInvoiceEmail(@PathVariable Long saleId) {
        try {
            Sale sale = saleService.getSaleById(saleId);
            byte[] pdfBytes = pdfService.generateSaleInvoice(sale);
            Client client = sale.getClient();

            if (client.getEmail() == null || client.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body("Client email is not available for this sale.");
            }

            String subject = "Your Carhub Invoice - #" + sale.getInvoiceNumber();
            String body = "Dear " + client.getFullName() + ",<br><br>" +
                          "Thank you for your recent purchase from Carhub. Please find your invoice attached.<br><br>" +
                          "Best regards,<br>Carhub Team";
            
            emailService.sendEmailWithAttachment(
                client.getEmail(), 
                subject, 
                body, 
                pdfBytes, 
                "invoice-" + sale.getInvoiceNumber() + ".pdf", 
                MediaType.APPLICATION_PDF_VALUE
            );

            return ResponseEntity.ok("Invoice email sent successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate PDF or send email: " + e.getMessage());
        }
    }
}
