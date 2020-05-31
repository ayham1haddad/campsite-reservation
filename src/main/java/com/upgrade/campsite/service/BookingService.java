package com.upgrade.campsite.service;

import com.upgrade.campsite.model.Booking;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    List<LocalDate> findAvailableDates(LocalDate startDate, LocalDate endDate);

    Booking findBookingById(Long id);

    Booking createBooking(Booking booking);

    Booking updateBooking(Long id, Booking booking);

    boolean cancelBooking(Long id);
}