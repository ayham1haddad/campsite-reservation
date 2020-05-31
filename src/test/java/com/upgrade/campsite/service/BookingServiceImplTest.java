package com.upgrade.campsite.service;

import com.upgrade.campsite.TestHelper;
import com.upgrade.campsite.exception.BookingDatesNotAvailableException;
import com.upgrade.campsite.exception.BookingNotFoundException;
import com.upgrade.campsite.exception.IllegalBookingStateException;
import com.upgrade.campsite.model.Booking;
import com.upgrade.campsite.repository.BookingRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private TestHelper testHelper = new TestHelper();

    @Test(expected = BookingNotFoundException.class)
    public void findBookingById_whenNonExistentBooking_bookingNotFoundExceptionThrown() {
        long id = 1L;
        doReturn(Optional.empty()).when(bookingRepository).findById(id);
        bookingService.findBookingById(id);
    }

    @Test
    public void findBookingById_whenExistingBooking_bookingFound() {

        long id = 1L;
        Booking persistedBooking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2));
        doReturn(Optional.of(persistedBooking)).when(bookingRepository).findById(id);
        Booking booking = bookingService.findBookingById(id);
        assertThat(booking).isEqualTo(persistedBooking);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findVacantDate_startRangeDateIsNow_illegalArgumentExceptionThrown() {

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(2);
        bookingService.findAvailableDates(startDate, endDate);

    }

    @Test
    public void findVacantDates_rangeDatesInsideBookingDates_noVacantDates() {
        LocalDate startDate = LocalDate.now().plusDays(2);
        LocalDate endDate = LocalDate.now().plusDays(3);
        Booking persistedBooking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4));
        doReturn(Collections.singletonList(persistedBooking))
                .when(bookingRepository).findByDateRange(startDate, endDate);
        // then
        List<LocalDate> vacantDates = bookingService.findAvailableDates(startDate, endDate);
        // when
        assertThat(vacantDates).isEmpty();
    }


    @Test
    public void findVacantDates_bookingDatesSameAsDateRange_onlyEndDateAvailable() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(4);
        Booking persistedBooking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4));

        doReturn(Collections.singletonList(persistedBooking))
                .when(bookingRepository).findByDateRange(startDate, endDate);

        List<LocalDate> vacantDates = bookingService.findAvailableDates(startDate, endDate);
        assertThat(vacantDates).size().isEqualTo(1);
        assertThat(vacantDates).contains(endDate);
    }

    @Test
    public void findVacantDates_noBookingRegistered_vacantDatesFromStartDateToEndDateInclusive() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(4);
        doReturn(Collections.emptyList())
                .when(bookingRepository).findByDateRange(startDate, endDate);
        List<LocalDate> vacantDates = bookingService.findAvailableDates(startDate, endDate);
        List<LocalDate> expected = startDate
                .datesUntil(endDate.plusDays(1)) // include end Date as available
                .collect(Collectors.toList());
        assertThat(vacantDates).isEqualTo(expected);
    }

    @Test(expected = BookingDatesNotAvailableException.class)
    public void registerBooking_bookingDatesNotAvailable_bookingDatesNotAvailableExceptionThrown() {

        Booking desiredBooking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4));

        Booking persistedBooking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2));

        doReturn(Collections.singletonList(persistedBooking))
                .when(bookingRepository).findByDateRange(desiredBooking.getStartDate(), desiredBooking.getEndDate());

        bookingService.createBooking(desiredBooking);
    }


    @Test
    public void registerBooking_bookingDatesAvailable_bookingCreated() {

        Booking desiredBooking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4));

        doReturn(Collections.emptyList())
                .when(bookingRepository).findByDateRange(desiredBooking.getStartDate(), desiredBooking.getEndDate());

        bookingService.createBooking(desiredBooking);

        verify(bookingRepository,times(1)).save(desiredBooking);
    }

    @Test(expected =  IllegalBookingStateException.class)
    public void updateBooking_bookingIsCancelled_illegalBookingStateExceptionThrown() {

        Long id = 1L;
        Booking updatingBooking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4));

        Booking persistedBooking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3));

        persistedBooking.setId(id);
        persistedBooking.setActive(false);

        doReturn(Optional.of(persistedBooking)).when(bookingRepository).findById(id);

        bookingService.updateBooking(id, updatingBooking);

    }

    @Test
    public void updateBooking_bookingDatesAvailable_bookingUpdated() {

        Long id = 1L;
        Booking desiredBooking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3));

        Booking persistedBooking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(4),
                LocalDate.now().plusDays(6));

        persistedBooking.setId(id);

        doReturn(Optional.of(persistedBooking)).when(bookingRepository).findById(id);

        doReturn(Collections.emptyList()).when(bookingRepository).findByDateRange(desiredBooking.getStartDate(),desiredBooking.getEndDate());

        bookingService.updateBooking(id, desiredBooking);
        verify(bookingRepository,times(1)).save(desiredBooking);

    }

    @Test(expected = BookingDatesNotAvailableException.class)
    public void updateBooking_bookingDatesNotAvailable_BookingDatesNotAvailableExceptionThrown() {

        Long id = 1L;
        Booking desiredBooking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3));

        Booking otherPersistedBooking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3));

        Booking persistedBooking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(9));

        persistedBooking.setId(id);

        doReturn(Optional.of(persistedBooking)).when(bookingRepository).findById(id);

        doReturn(Collections.singletonList(otherPersistedBooking)).when(bookingRepository).findByDateRange(desiredBooking.getStartDate(),desiredBooking.getEndDate());

        bookingService.updateBooking(id, desiredBooking);

    }

    @Test
    public void cancelBooking_activeFlaggedSwitchedToFalse() {

        long id = 1L;

        ArgumentCaptor<Booking> argumentCaptor = ArgumentCaptor.forClass(Booking.class);
        Booking persistedBooking = testHelper.buildBooking(
                "John","Doe",
                "john.doe@nowhere.com",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4));

        persistedBooking.setId(id);
        persistedBooking.setActive(true);

        doReturn(Optional.of(persistedBooking))
                .when(bookingRepository).findById(id);

        doReturn(persistedBooking)
                .when(bookingRepository).save(persistedBooking);

        bookingService.cancelBooking(id);

        verify(bookingRepository,times(1)).save(argumentCaptor.capture());
        assertThat(Boolean.FALSE).isEqualTo(argumentCaptor.getValue().isActive());


    }



}
