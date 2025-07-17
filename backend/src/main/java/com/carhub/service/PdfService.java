package com.carhub.service;

import com.carhub.entity.Car;
import com.carhub.entity.Sale;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class PdfService {

    public byte[] generateCarInventoryReport(List<Car> cars) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        // Set fonts
        PdfFont titleFont = PdfFontFactory.createFont();
        PdfFont headerFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();

        // Title
        Paragraph title = new Paragraph("CAR INVENTORY REPORT")
                .setFont(titleFont)
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        // Date
        Paragraph date = new Paragraph("Generated on: " + 
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20);
        document.add(date);

        // Add separator
        document.add(new LineSeparator(new SolidLine())); // Correction
        document.add(new Paragraph("\n"));


        // Summary
        long totalCars = cars.size();
        long availableCars = cars.stream().filter(car -> car.getStatus() == Car.Status.AVAILABLE).count();
        long soldCars = cars.stream().filter(car -> car.getStatus() == Car.Status.SOLD).count();

        Paragraph summary = new Paragraph("SUMMARY")
                .setFont(headerFont)
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10);
        document.add(summary);

        Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        summaryTable.addHeaderCell(new Cell().add(new Paragraph("Total Cars").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        summaryTable.addHeaderCell(new Cell().add(new Paragraph("Available").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        summaryTable.addHeaderCell(new Cell().add(new Paragraph("Sold").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));

        summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalCars))));
        summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(availableCars))));
        summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(soldCars))));

        document.add(summaryTable);
        document.add(new Paragraph("\n"));

        // Cars table
        Paragraph carsTitle = new Paragraph("CAR DETAILS")
                .setFont(headerFont)
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10);
        document.add(carsTitle);

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 1, 2, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        // Table headers
        table.addHeaderCell(new Cell().add(new Paragraph("Brand").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Model").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Year").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Price").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Color").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Status").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));

        // Table data
        for (Car car : cars) {
            table.addCell(new Cell().add(new Paragraph(car.getBrand())));
            table.addCell(new Cell().add(new Paragraph(car.getModel())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(car.getYear()))));
            table.addCell(new Cell().add(new Paragraph("$" + car.getPrice().toString())));
            table.addCell(new Cell().add(new Paragraph(car.getColor())));
            
            Cell statusCell = new Cell().add(new Paragraph(car.getStatus().toString()));
            if (car.getStatus() == Car.Status.AVAILABLE) {
                statusCell.setBackgroundColor(ColorConstants.GREEN).setFontColor(ColorConstants.WHITE);
            } else if (car.getStatus() == Car.Status.SOLD) {
                statusCell.setBackgroundColor(ColorConstants.RED).setFontColor(ColorConstants.WHITE);
            }
            table.addCell(statusCell);
        }

        document.add(table);

        // Footer
        document.add(new LineSeparator(new SolidLine())); // ✅ Correction
        document.add(new Paragraph("\n"));

        Paragraph footer = new Paragraph("Carhub - Car Dealership Management System")
                .setFont(normalFont)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }

    public byte[] generateSaleInvoice(Sale sale) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        // Set fonts
        PdfFont titleFont = PdfFontFactory.createFont();
        PdfFont headerFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();

        // Header
        Paragraph header = new Paragraph("CARHUB DEALERSHIP")
                .setFont(titleFont)
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(header);

        Paragraph subHeader = new Paragraph("SALES INVOICE")
                .setFont(headerFont)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subHeader);

        // Invoice details
        Table invoiceInfo = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        invoiceInfo.addCell(new Cell().add(new Paragraph("Invoice Number: " + sale.getInvoiceNumber()).setBold())
                .setBorder(Border.NO_BORDER));
        invoiceInfo.addCell(new Cell().add(new Paragraph("Date: " + 
                sale.getSaleDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).setBold())
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(invoiceInfo);
        document.add(new Paragraph("\n"));

        // Client information
        Paragraph clientTitle = new Paragraph("BILL TO:")
                .setFont(headerFont)
                .setFontSize(12)
                .setBold();
        document.add(clientTitle);

        Paragraph clientInfo = new Paragraph()
                .add(sale.getClient().getFirstName() + " " + sale.getClient().getLastName() + "\n")
                .add(sale.getClient().getEmail() + "\n")
                .add(sale.getClient().getPhone() != null ? sale.getClient().getPhone() + "\n" : "")
                .add(sale.getClient().getAddress() != null ? sale.getClient().getAddress() + "\n" : "")
                .setFont(normalFont)
                .setMarginBottom(20);
        document.add(clientInfo);

        // Car details
        Paragraph carTitle = new Paragraph("VEHICLE DETAILS:")
                .setFont(headerFont)
                .setFontSize(12)
                .setBold();
        document.add(carTitle);

        Table carTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        carTable.addCell(new Cell().add(new Paragraph("Brand:").setBold()));
        carTable.addCell(new Cell().add(new Paragraph(sale.getCar().getBrand())));
        carTable.addCell(new Cell().add(new Paragraph("Model:").setBold()));
        carTable.addCell(new Cell().add(new Paragraph(sale.getCar().getModel())));
        carTable.addCell(new Cell().add(new Paragraph("Year:").setBold()));
        carTable.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getCar().getYear()))));
        carTable.addCell(new Cell().add(new Paragraph("Color:").setBold()));
        carTable.addCell(new Cell().add(new Paragraph(sale.getCar().getColor())));
        carTable.addCell(new Cell().add(new Paragraph("VIN:").setBold()));
        carTable.addCell(new Cell().add(new Paragraph(sale.getCar().getVin() != null ? sale.getCar().getVin() : "N/A")));

        document.add(carTable);
        document.add(new Paragraph("\n"));

        // Payment details
        Table paymentTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        paymentTable.addHeaderCell(new Cell().add(new Paragraph("Description").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        paymentTable.addHeaderCell(new Cell().add(new Paragraph("Amount").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.RIGHT));

        paymentTable.addCell(new Cell().add(new Paragraph("Vehicle Sale Price")));
        paymentTable.addCell(new Cell().add(new Paragraph("$" + sale.getSalePrice().toString()))
                .setTextAlignment(TextAlignment.RIGHT));

        // Total
        paymentTable.addCell(new Cell().add(new Paragraph("TOTAL").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        paymentTable.addCell(new Cell().add(new Paragraph("$" + sale.getSalePrice().toString()).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(paymentTable);
        document.add(new Paragraph("\n"));

        // Payment method
        Paragraph paymentMethod = new Paragraph("Payment Method: " + sale.getPaymentMethod().toString())
                .setFont(normalFont)
                .setBold();
        document.add(paymentMethod);

        // Notes
        if (sale.getNotes() != null && !sale.getNotes().isEmpty()) {
            document.add(new Paragraph("\n"));
            Paragraph notesTitle = new Paragraph("Notes:")
                    .setFont(headerFont)
                    .setBold();
            document.add(notesTitle);
            
            Paragraph notes = new Paragraph(sale.getNotes())
                    .setFont(normalFont);
            document.add(notes);
        }

        // Footer
        document.add(new LineSeparator(new SolidLine())); // ✅ Correction
        document.add(new Paragraph("\n"));

        Paragraph footer = new Paragraph("Thank you for your business!")
                .setFont(normalFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);
        document.add(footer);

        Paragraph contact = new Paragraph("Contact: info@carhub.com | Phone: (555) 123-4567")
                .setFont(normalFont)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(contact);

        document.close();
        return baos.toByteArray();
    }

    public byte[] generateMonthlySalesReport(List<Sale> sales, String month, String year) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        // Title
        Paragraph title = new Paragraph("MONTHLY SALES REPORT")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(title);

        Paragraph period = new Paragraph("Period: " + month + " " + year)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(period);

        // Summary
        double totalRevenue = sales.stream()
                .mapToDouble(sale -> sale.getSalePrice().doubleValue())
                .sum();
        
        Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(50))
                .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);

        summaryTable.addHeaderCell(new Cell().add(new Paragraph("Total Sales").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        summaryTable.addHeaderCell(new Cell().add(new Paragraph("Total Revenue").setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));

        summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(sales.size()))));
        summaryTable.addCell(new Cell().add(new Paragraph("$" + String.format("%.2f", totalRevenue))));

        document.add(summaryTable);
        document.add(new Paragraph("\n"));

        // Sales details
        if (!sales.isEmpty()) {
            Table salesTable = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 1, 1}))
                    .setWidth(UnitValue.createPercentValue(100));

            salesTable.addHeaderCell(new Cell().add(new Paragraph("Date").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            salesTable.addHeaderCell(new Cell().add(new Paragraph("Vehicle").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            salesTable.addHeaderCell(new Cell().add(new Paragraph("Client").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            salesTable.addHeaderCell(new Cell().add(new Paragraph("Price").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            salesTable.addHeaderCell(new Cell().add(new Paragraph("Payment").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));

            for (Sale sale : sales) {
                salesTable.addCell(new Cell().add(new Paragraph(
                        sale.getSaleDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))));
                salesTable.addCell(new Cell().add(new Paragraph(
                        sale.getCar().getBrand() + " " + sale.getCar().getModel())));
                salesTable.addCell(new Cell().add(new Paragraph(
                        sale.getClient().getFirstName() + " " + sale.getClient().getLastName())));
                salesTable.addCell(new Cell().add(new Paragraph("$" + sale.getSalePrice().toString())));
                salesTable.addCell(new Cell().add(new Paragraph(sale.getPaymentMethod().toString())));
            }

            document.add(salesTable);
        }

        document.close();
        return baos.toByteArray();
    }
}
