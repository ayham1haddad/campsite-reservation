package com.upgrade.campsite.repository;

import com.upgrade.campsite.model.Booking;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select b from Booking b "
            + "where ((b.startDate < ?1 and ?2 < b.endDate) "
            + "or (b.endDate > ?1 and b.endDate <= ?2) "
            + "or (b.startDate >= ?1 and b.startDate <= ?2)) "
            + "and b.active = true "
            + "order by b.startDate asc")
    List<Booking> findByDateRange(LocalDate startDate, LocalDate endDate);
}
