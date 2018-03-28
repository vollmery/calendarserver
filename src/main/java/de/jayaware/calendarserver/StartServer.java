package de.jayaware.calendarserver;

import de.jayaware.calendarserver.utils.SessionManagement;

public class StartServer {

	public static void main(String[] args) {

		CalendarServer calendarServer = new CalendarServer(SessionManagement.expiresIn(30 * 60 * 1000));
		calendarServer.start(null);
	}

}
