package com.fantasy.tbs.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fantasy.tbs.domain.TimeBooking;

public interface HumanResourcesService {

	/**
	 * Calculates the total work duration for an employee given personalNumber.
	 * If startDate and/or endDate are given, retrieves only the TimeBookings between those dates
	 * @param personalNumber
	 * @param endDate 
	 * @param startDate 
	 * @return total Duration
	 */
	Duration getWorkedTimeByPersonalNumber(String personalNumber, LocalDate startDate, LocalDate endDate);
	
	public Map<LocalDate, List<TimeBooking>> getGroupedTimeBookingsByPersonalNumber(String personalNumber,
			LocalDate startDate, LocalDate endDate);
	
}
