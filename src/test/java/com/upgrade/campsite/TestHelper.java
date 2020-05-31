package com.upgrade.campsite;

import com.upgrade.campsite.model.Booking;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TestHelper {

    public Booking buildBooking(
            String firstName,String lastName, String email, LocalDate startDate, LocalDate endDate) {
        return Booking.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .startDate(startDate)
                .endDate(endDate)
                .active(true)
                .build();
    }
}