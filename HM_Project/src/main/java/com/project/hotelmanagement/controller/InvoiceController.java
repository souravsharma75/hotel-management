package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("hotels/bookings")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("{bookingId}/invoice")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long bookingId) {

        byte[] pdf = invoiceService.generateInvoice(bookingId);

        return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=invoice-"
                        + bookingId+".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
