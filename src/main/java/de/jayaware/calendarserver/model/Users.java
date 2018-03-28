package de.jayaware.calendarserver.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Users {

	private List<User> users = new ArrayList<>();
}
