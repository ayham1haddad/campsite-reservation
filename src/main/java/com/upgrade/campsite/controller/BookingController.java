package com.upgrade.campsite.controller;

import com.upgrade.campsite.model.Booking;
import com.upgrade.campsite.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService){
        this.bookingService = bookingService;
    }

    @GetMapping(value = "/available-dates",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<java.util.List<LocalDate>> getVacantDates(
            @RequestParam(name = "start_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        //default startdate and enddate are month apart.
        if (startDate == null) {
            startDate = LocalDate.now().plusDays(1);
        }
        if (endDate == null) {
            endDate = startDate.plusMonths(1);
        }
        java.util.List<LocalDate> vacantDates = bookingService.findAvailableDates(startDate, endDate);
        return new ResponseEntity<>(vacantDates, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Booking> getBooking(@PathVariable() long id) {

        Booking booking = bookingService.findBookingById(id);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Booking> registerBooking(@RequestBody @Valid Booking booking) {

        Booking addedBooking = bookingService.createBooking(booking);

        return new ResponseEntity<>(addedBooking, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Booking> updateBooking(
            @PathVariable("id") long id, @RequestBody @Valid Booking booking) {

        Booking updateBooking = bookingService.updateBooking(id, booking);

        return new ResponseEntity<>(updateBooking, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> cancelBooking(@PathVariable("id") long id) {

        boolean cancelled = bookingService.cancelBooking(id);
        if (cancelled) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }




}
