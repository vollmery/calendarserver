package de.jayaware.calendarserver.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Login {
	private String username;
	private String password;
}
