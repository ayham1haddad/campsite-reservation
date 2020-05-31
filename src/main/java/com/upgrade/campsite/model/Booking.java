package com.upgrade.campsite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upgrade.campsite.model.validator.AllowedStartDate;
import com.upgrade.campsite.model.validator.MaximumDaysStay;
import com.upgrade.campsite.model.validator.StartDateBeforeEndDate;
import java.time.LocalDate;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@Data
    @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@StartDateBeforeEndDate
@AllowedStartDate
@MaximumDaysStay
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotEmpty(message = "email cannot be empty")
    @Column(name = "email", nullable = false, length = 50)
    private String email;


    @NotEmpty(message = "first name cannot be empty")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;


    @NotEmpty(message = "last name cannot be empty")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;


    @Future(message = "start_date has to be in the future")
    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;


    @Future(message = "end_date has to be in the future")
    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;


    @Column(name = "active", nullable = false)
    private boolean active;

    @JsonIgnore
    public java.util.List<LocalDate> getBookingDates(){
        return startDate.datesUntil(endDate).collect(Collectors.toList());
    }

}