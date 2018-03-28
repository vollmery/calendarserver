package de.jayaware.calendarserver.model;

import lombok.Data;

@Data
public class User {

	private final String id;
	private String name;
	private byte[] password;
	private boolean active = Boolean.TRUE;
}
