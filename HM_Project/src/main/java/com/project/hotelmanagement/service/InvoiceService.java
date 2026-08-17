package com.project.hotelmanagement.service;

import org.springframework.stereotype.Service;

@Service
public interface InvoiceService {

    byte[] generateInvoice(Long bookingId);
}
