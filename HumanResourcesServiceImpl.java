package com.fantasy.tbs.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fantasy.tbs.domain.TimeBooking;
import com.fantasy.tbs.repository.TimeBookingRepository;
import com.fantasy.tbs.service.HumanResourcesService;
import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class HumanResourcesServiceImpl implements HumanResourcesService {
	
	private final TimeBookingRepository timeBookingRepository;
	private final Logger logger = LoggerFactory.getLogger(TimeBookingServiceImpl.class);
	
	public HumanResourcesServiceImpl(TimeBookingRepository timeBookingRepository) {
		this.timeBookingRepository = timeBookingRepository;
	}

	@Override
	@Transactional	
	/**
	 * Calculates the total work duration for an employee given personalNumber 
	 * If startDate and/or endDate are given, retrieves only the TimeBookings between those dates
	 * @param personalNumber
	 * @return total Duration
	 */
	public Duration getWorkedTimeByPersonalNumber(String personalNumber, LocalDate startDate, LocalDate endDate) {
		
		var bookingsPerDay = getGroupedTimeBookingsByPersonalNumber(personalNumber,	startDate, endDate);
        
		if (bookingsPerDay == null) return null;
		
        Duration totalDuration = Duration.ZERO;
        
        for (var date : bookingsPerDay.keySet()) {
            var bookings = bookingsPerDay.get(date);
                        
            if (bookings.size() > 1 && bookings.size() % 2 == 0) {
                for (int i = 1; i < bookings.size(); i += 2) {
                    totalDuration = totalDuration.plus(Duration.between(bookings.get(i).getBooking(), bookings.get(i - 1).getBooking()));
                }
            }
            else {
            	//In this case a closing time booking is missing. I will will add only the times between closed bookings
                for (int i = 0; i < bookings.size() - 1; i += 2) {
                    totalDuration = totalDuration.plus(Duration.between(bookings.get(i + 1).getBooking(), bookings.get(i).getBooking()));
                }
                
                logger.warn("Found open booking for employee {} on {}", personalNumber, date.toString());
            }
        }
        
        return totalDuration;
	}

	@Override
	public Map<LocalDate, List<TimeBooking>> getGroupedTimeBookingsByPersonalNumber(String personalNumber,
			LocalDate startDate, LocalDate endDate) {
		var allTimeBookings = timeBookingRepository.getTimeBookingsByPersonalNumber(personalNumber, startDate, endDate);
		
		if (allTimeBookings == null || allTimeBookings.isEmpty()) {
			logger.warn("Time bookings not found for employee {}", personalNumber);
			return null;
		}

		//I assume start and end time booking must be done on the same day (i.e. no night shifts)
        Map<LocalDate, List<TimeBooking>> bookingsPerDay = allTimeBookings
            .stream()
            .collect(
                groupingBy(
                    timeBooking -> {
                        return timeBooking.getBooking().toLocalDate();
                    }
                )
            );
        
		return bookingsPerDay;
	}

	@Override
	public List<String> getWorkingEmployeesByDate(LocalDate date) {
		// TODO: implement logic
		return new ArrayList<String>();
	}

}
