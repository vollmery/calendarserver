package de.jayaware.calendarserver;

import com.google.gson.Gson;

import de.jayaware.calendarserver.model.Login;
import de.jayaware.calendarserver.model.LoginResult;
import de.jayaware.calendarserver.model.User;
import de.jayaware.calendarserver.utils.SessionManagement;
import de.jayaware.calendarserver.utils.UserManagement;
import lombok.Value;
import spark.Spark;

@Value
public class CalendarServer {

	private final Integer defaultPort = 8833;
	private final UserManagement userManagement = UserManagement.getInstance();
	private final SessionManagement sessionManagement;
	private final Gson gson = new Gson();

	public void start(Integer givenPort) {
		Spark.port(givenPort == null ? defaultPort.intValue() : givenPort.intValue());
		Spark.staticFiles.externalLocation(System.getProperty("user.dir") + "/public");
		Spark.get("/hello", (req, res) -> "herbert");

		Spark.post("/login", (req, res) -> {
			Login login = gson.fromJson(req.body(), Login.class);
			User authenticatedUser = userManagement.validLogin(login.getUsername(), login.getPassword());
			if (authenticatedUser == null) {
				return CalendarServerErrors.UNKNOWN_USER.send(res);
			}
			String token = sessionManagement.createTokenFor(authenticatedUser);
			return gson.toJson(LoginResult.builder().token(token).build());
		});

		Spark.get(":token/getTermine", (req, res) -> {
			return "";
		});

	}

}
