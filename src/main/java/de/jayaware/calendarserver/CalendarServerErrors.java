package de.jayaware.calendarserver;

import de.jayaware.calendarserver.model.ErrorResponse;

public class CalendarServerErrors {

	public static final ErrorResponse UNKNOWN_USER = ErrorResponse.builder().code(404)
			.message("Fick dich du Heckenpenner!").build();

}
