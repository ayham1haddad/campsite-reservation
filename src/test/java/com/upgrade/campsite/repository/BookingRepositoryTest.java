package com.upgrade.campsite.repository;

import com.upgrade.campsite.TestHelper;
import com.upgrade.campsite.model.Booking;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestHelper testHelper;

    @Test
    public void findById_persistBooking_BookingFound() {
        Booking savedBooking = bookingRepository.save(testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)));
        Optional<Booking> foundBooking = bookingRepository.findById(savedBooking.getId());
        assertThat(foundBooking.get()).isEqualTo(savedBooking);
    }

    @Test
    public void findBooking_bookingDatesAndRangesDontOverlap1_noBookingFound() {
        bookingRepository.save(testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)));
        List<Booking> bookings = bookingRepository.findByDateRange(
                LocalDate.now().plusDays(3), LocalDate.now().plusDays(4));
        assertThat(bookings).isEmpty();
    }


    @Test
    public void findBooking_bookingDatesSameAsRangeDate_bookingFound() {
        Booking savedBooking = bookingRepository.save(testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4)));
        List<Booking> bookings = bookingRepository.findByDateRange(
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(4));
        assertThat(bookings).size().isEqualTo(1);
        assertThat(savedBooking).isIn(bookings);
    }


    @Test
    public void findBooking_bookingStartDateBeforeStartRangeDateAndBookingEndDateEqualsStartDateRange_noBookingFound() {
        Booking savedBooking = bookingRepository.save(testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)));
        List<Booking> bookings = bookingRepository.findByDateRange(
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(3));
        assertThat(bookings).isEmpty();
    }

    @Test
    public void findBooking_bookingStartDateBeforeStartDateRangeAndBookingEndDateBeforeEndDateRange_bookingFound() {
        Booking savedBooking = bookingRepository.save(testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)));
        List<Booking> bookings = bookingRepository.findByDateRange(
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));
        assertThat(bookings).size().isEqualTo(1);
        assertThat(savedBooking).isIn(bookings);
    }

    @Test
    public void findBooking_bookingStartDateAfterStartDateRangeAndBookingEndDateAfterEndRangeDate_bookingFound() {

        Booking savedBooking = bookingRepository.save(testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(4)));
        List<Booking> bookings = bookingRepository.findByDateRange(
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        assertThat(bookings).size().isEqualTo(1);
        assertThat(savedBooking).isIn(bookings);
    }

    @Test
    public void findBooking_bookingDatesAndRangesDontOverlap2_noBookingFound() {
        Booking savedBooking = bookingRepository.save(testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(4)));
        List<Booking> bookings = bookingRepository.findByDateRange(
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        assertThat(bookings).isEmpty();
    }

    @Test
    public void findBooking_bookingDatesIncludeRangeDates_bookingFound() {
        Booking savedBooking = bookingRepository.save(testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4)));
        List<Booking> bookings = bookingRepository.findByDateRange(
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(3));
        assertThat(bookings).size().isEqualTo(1);
        assertThat(savedBooking).isIn(bookings);
    }

    @Test
    public void findBooking_bookingStartDateEqualsEndDateRange_bookingFound() {
        Booking savedBooking = bookingRepository.save(testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5)));
        List<Booking> bookings = bookingRepository.findByDateRange(
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        assertThat(bookings).size().isEqualTo(1);
        assertThat(savedBooking).isIn(bookings);
    }

    @Test
    public void findBooking_bookingEndDateEqualsStartDateRange_bookingFound() {
        Booking savedBooking = bookingRepository.save(testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5)));
        List<Booking> bookings = bookingRepository.findByDateRange(
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));
        assertThat(bookings).isEmpty();
    }
    @Test
    public void findBooking_bookingRangeDateInsideTheDateRange_bookingFound() {
        Booking savedBooking = bookingRepository.save(testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5)));
        List<Booking> bookings = bookingRepository.findByDateRange(
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(10));
        assertThat(bookings).size().isEqualTo(1);
        assertThat(savedBooking).isIn(bookings);
    }

    @Test
    public void findBooking_bothStartDateSameAndEndDateDifferent_bookingFound() {
        Booking savedBooking = bookingRepository.save(testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)));
        List<Booking> bookings = bookingRepository.findByDateRange(
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        assertThat(bookings).size().isEqualTo(1);
        assertThat(savedBooking).isIn(bookings);
    }





}
