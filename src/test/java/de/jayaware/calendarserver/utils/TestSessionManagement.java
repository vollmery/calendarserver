package de.jayaware.calendarserver.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jayaware.calendarserver.model.User;

public class TestSessionManagement {

	private static final int EXPIRES_IN_MILLIS = 200;
	private static String userName = "fred feuerstein";
	private static String userPassword = "meinPasswort";
	private static UserManagement userManagement = UserManagement.getInstance();
	private static User user;

	private SessionManagement systemUnderTest = SessionManagement.expiresIn(EXPIRES_IN_MILLIS);

	@BeforeClass
	public static void createUsersForTest() throws Exception {
		userManagement.add(userName, userPassword);
		user = userManagement.validLogin(userName, userPassword);
	}

	@Test
	public void canHandleSessionTokensPerfectly() throws InterruptedException {

		String token = systemUnderTest.createTokenFor(user);
		assertThat(token).isNotEmpty();

		assertThat(systemUnderTest.valid(token)).isTrue();

		User user = systemUnderTest.getUser(token);
		assertThat(user.getId()).isEqualTo(user.getId());

		Thread.sleep(EXPIRES_IN_MILLIS);
		assertThat(systemUnderTest.valid(token)).isFalse();
	}

	@AfterClass
	public static void cleanup() {
		userManagement.removeFromFile(user);
	}

}
