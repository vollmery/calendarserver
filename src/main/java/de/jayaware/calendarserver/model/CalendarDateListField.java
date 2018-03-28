package de.jayaware.calendarserver.model;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CalendarDateListField {

	private String userId;
	private long start;
	private long end;
	private String subject;
	private String body;
	private List<String> invited;

}
