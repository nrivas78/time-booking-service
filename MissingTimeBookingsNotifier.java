package com.fantasy.tbs.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fantasy.tbs.domain.TimeBooking;
import com.fantasy.tbs.service.HumanResourcesService;
import com.fantasy.tbs.service.impl.TimeBookingServiceImpl;

@Component
public class MissingTimeBookingsNotifier {

	@Autowired
	private HumanResourcesService humanResourcesService;
	
	@Autowired
	private JavaMailSender javaMailSender;

	private final Logger logger = LoggerFactory.getLogger(TimeBookingServiceImpl.class);
	
	@Scheduled(cron = "0 1 * * *")
	public void notifyMissingTimeBookings() {
		

		logger.info("Started automatic notification of missing TimeBookings");
		var currentTime = LocalDate.now();
		var previousDay = currentTime.minusDays(1);
		var employeesWithMissingBookings = new ArrayList<String>();
		
		for (var personalNumber : humanResourcesService.getWorkingEmployeesByDate(previousDay)) {
			
			var bookingsPerDay = humanResourcesService.getGroupedTimeBookingsByPersonalNumber(personalNumber, previousDay, previousDay);
			
			if (hasMissingTimeBookings(previousDay, bookingsPerDay)) {
				logger.info("Found missing TimeBookings for employee {} on {}", personalNumber, previousDay.toString());
				employeesWithMissingBookings.add(personalNumber);
			}
		}
		
		sendNotification(employeesWithMissingBookings, previousDay);
	}

	private boolean hasMissingTimeBookings(LocalDate previousDay, Map<LocalDate, List<TimeBooking>> bookingsPerDay) {
		return bookingsPerDay == null 
				|| bookingsPerDay.isEmpty() 
				|| !bookingsPerDay.containsKey(previousDay) 
				|| bookingsPerDay.get(previousDay).isEmpty() 
				|| bookingsPerDay.get(previousDay).size() % 2 != 0;
	}

	private void sendNotification(List<String> personalNumbers, LocalDate date) {
				
		var simpleMailMessage = new SimpleMailMessage();
		//TODO: retrieve addressee's email address and other data
		//TODO: notify individual employees?
		simpleMailMessage.setTo("hr@fantasy.com");
		
		if (personalNumbers.size() == 0) {
			simpleMailMessage.setText(String.format("No missing TimeBookings found on %s", date.toString()));
		}
		else {
			simpleMailMessage.setText(String.format("The following employees have missing TimeBookings found on %s:\\n%s", date.toString(), String.join(",", personalNumbers)));
		}			
		
		javaMailSender.send(simpleMailMessage);
		
	}
}
