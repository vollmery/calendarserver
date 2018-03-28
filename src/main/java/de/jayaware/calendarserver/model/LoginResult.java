package de.jayaware.calendarserver.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginResult {

	private String token;

}
