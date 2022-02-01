package com.fantasy.tbs.web.rest;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fantasy.tbs.service.HumanResourcesService;

@RestController
@RequestMapping("/api")
public class HumanResourcesController {
	
	private final HumanResourcesService humanResourcesService;
	
	public HumanResourcesController(HumanResourcesService humanResourcesService) {
		this.humanResourcesService = humanResourcesService;
	}

	@GetMapping("/humanResources/{personalNumber}/workedHours")
	/**
	 * Returns the total worked hours for an employee given personalNumber 
	 * @param personalNumber
	 * @return total Duration
	 */
	public ResponseEntity<String> getWorkedHoursByPersonalNumber(@PathVariable(value = "personalNumber", required = true) final String personalNumber, 
			@RequestParam(value="startDate", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") final LocalDate startDate,  
			@RequestParam(value="endDate", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") final LocalDate endDate) {
		
		var duration = humanResourcesService.getWorkedTimeByPersonalNumber(personalNumber, startDate, endDate);
		
		try {
			if (duration == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			
			var workedHours = duration.toHours();
			
			return ResponseEntity.ok(String.valueOf(workedHours));
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("Error processing request: %s", e.getMessage()));
		}		
	}
}
