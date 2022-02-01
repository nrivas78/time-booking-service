package com.fantasy.tbs.repository;

import com.fantasy.tbs.domain.TimeBooking;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TimeBooking entity.
 */
//@SuppressWarnings("unused")
@Repository
public interface TimeBookingRepository extends JpaRepository<TimeBooking, Long> {

	/**
	 * Returns all TimeBookings for a given personalNumber. 
	 * If startDate and/or endDate are given, retrieves only the TimeBookings between those dates
	 * @param personalNumber
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<TimeBooking> getTimeBookingsByPersonalNumber(String personalNumber, LocalDate startDate, LocalDate endDate);
}
