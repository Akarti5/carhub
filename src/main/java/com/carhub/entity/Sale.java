package com.carhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "invoice_number", unique = true)
    private String invoiceNumber;

    @Column(name = "sale_date")
    private LocalDateTime saleDate = LocalDateTime.now();

    @Column(length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    private Status status = Status.COMPLETED;

    public enum PaymentMethod {
        CASH, CREDIT_CARD, BANK_TRANSFER, FINANCING, CHECK
    }

    public enum Status {
        PENDING, COMPLETED, CANCELLED, REFUNDED
    }

    // Constructors
    public Sale() {}

    public Sale(Car car, Client client, Admin admin, BigDecimal salePrice, PaymentMethod paymentMethod) {
        this.car = car;
        this.client = client;
        this.admin = admin;
        this.salePrice = salePrice;
        this.paymentMethod = paymentMethod;
        this.invoiceNumber = generateInvoiceNumber();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }
}
