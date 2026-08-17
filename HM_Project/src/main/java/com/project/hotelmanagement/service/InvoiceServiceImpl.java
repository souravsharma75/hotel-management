package com.project.hotelmanagement.service;

import com.project.hotelmanagement.Entity.Booking;
import com.project.hotelmanagement.Entity.User;
import com.project.hotelmanagement.Entity.enums.BookingStatus;
import com.project.hotelmanagement.Entity.enums.PaymentStatus;
import com.project.hotelmanagement.exception.ResourceNotFoundException;
import com.project.hotelmanagement.repository.BookingRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final BookingRepository bookingRepository;

    @Override
    public byte[] generateInvoice(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with Id " + bookingId));

        User currentUser = getCurrentUser();

        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to access this invoice");
        }

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Invoice is available only for confirmed bookings");
        }

        if (booking.getPayment() == null ||
                booking.getPayment().getPaymentStatus()
                        != PaymentStatus.CONFIRMED) {

            throw new IllegalStateException(
                    "Payment is not confirmed");
        }

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(document, outputStream);

            document.open();

            document.add(
                    new Paragraph("HOTEL BOOKING INVOICE"));

            document.add(
                    new Paragraph(
                            "Invoice No: INV-" + booking.getId()));

            document.add(
                    new Paragraph(
                            "Booking ID: " + booking.getId()));

            document.add(
                    new Paragraph(
                            "Hotel: " +
                                    booking.getHotel().getName()));

            document.add(
                    new Paragraph(
                            "Room: " +
                                    booking.getRoom().getId()));

            document.add(
                    new Paragraph(
                            "Check-in: " +
                                    booking.getCheckIn()));

            document.add(
                    new Paragraph(
                            "Check-out: " +
                                    booking.getCheckOut()));

            document.add(
                    new Paragraph(
                            "Room Count: " +
                                    booking.getRoomCount()));

            document.add(
                    new Paragraph(
                            "Amount: ₹" +
                                    booking.getPayment().getAmount()));

            document.add(
                    new Paragraph(
                            "Payment ID: " +
                                    booking.getPayment()
                                            .getRazorpayPaymentId()));

            document.add(
                    new Paragraph(
                            "Payment Status: " +
                                    booking.getPayment()
                                            .getPaymentStatus()));

            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to generate invoice", e);
        }
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return (User) authentication.getPrincipal();
    }
}
