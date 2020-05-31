package com.upgrade.campsite.service;

import com.upgrade.campsite.exception.BookingDatesNotAvailableException;
import com.upgrade.campsite.exception.BookingNotFoundException;
import com.upgrade.campsite.exception.IllegalBookingStateException;
import com.upgrade.campsite.model.Booking;
import com.upgrade.campsite.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository) {

        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalDate> findAvailableDates(LocalDate startDate, LocalDate endDate) {

        LocalDate now = LocalDate.now();
        if(startDate.isBefore(now) || startDate.isEqual(now)){
            throw new IllegalArgumentException("startDate has to be in the future");
        }
        if(endDate.isBefore(now) || endDate.isEqual(now)){
            throw new IllegalArgumentException("endDate has to be in the future");
        }
        if(startDate.isAfter(endDate)){
            throw new IllegalArgumentException("startDate has to be before endDate");
        }
        List<LocalDate> availableDates = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList()); // all dates between start date and end date
        List<Booking> availableActiveBookings = bookingRepository.findByDateRange(startDate,endDate);

        availableActiveBookings.forEach(a -> availableDates.removeAll(a.getBookingDates()));

        return availableDates;
    }

    @Override
    @Transactional(readOnly =  true)
    public Booking findBookingById(Long id) {

        Optional<Booking> booking = bookingRepository.findById(id);
        if(!booking.isPresent()){
            throw new BookingNotFoundException(String.format("booking not found with id=%d",id));
        }
        return booking.get();
    }

    @Override
    @Transactional
    @Retryable(include = CannotAcquireLockException.class,backoff = @Backoff(delay = 150))
    public Booking createBooking(Booking booking) {

        List<LocalDate> availableDates = findAvailableDates(booking.getStartDate(),booking.getEndDate());

        if(!availableDates.containsAll(booking.getBookingDates())){
            String message = String.format("No available dates available from %s to %s",
                    booking.getStartDate(), booking.getEndDate());
            throw new BookingDatesNotAvailableException(message);
        }
        booking.setActive(true);

        return bookingRepository.save(booking);

    }

    @Override
    @Transactional
    public Booking updateBooking(Long id, Booking booking) {
        Booking persistedBooking = findBookingById(id);

        if(!persistedBooking.isActive()){
            throw new IllegalBookingStateException(String.format("Booking with id=%d was cancelled ",id));
        }
        List<LocalDate> availableVacantDates = findAvailableDates(booking.getStartDate(),booking.getEndDate());
        availableVacantDates.addAll(persistedBooking.getBookingDates());

        if(!availableVacantDates.containsAll(booking.getBookingDates())){
            String message = String.format("No available dates available from %s to %s",
                    booking.getStartDate(), booking.getEndDate());
            throw new BookingDatesNotAvailableException(message);
        }
        booking.setId(id);
        booking.setActive(persistedBooking.isActive());
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public boolean cancelBooking(Long id) {

        Booking booking = findBookingById(id);
        booking.setActive(false);
        booking = bookingRepository.save(booking);
        return !booking.isActive();
    }
}
