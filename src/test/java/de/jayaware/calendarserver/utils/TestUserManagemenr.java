package de.jayaware.calendarserver.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Test;

import com.google.gson.Gson;

import de.jayaware.calendarserver.exceptions.UserIsAlreadyKnown;
import de.jayaware.calendarserver.model.User;
import de.jayaware.calendarserver.model.Users;

public class TestUserManagemenr {

	private UserManagement systemUnderTest = UserManagement.getInstance();

	private final Gson gson = new Gson();

	@Test
	public void canAddNewUsersAndStoresThemToFileAndRemovesThem() throws Exception {
		String newUserName = "username";
		String newPassword = "password";
		systemUnderTest.add(newUserName, newPassword);

		Users allUsers = readFromFile();

		Optional<User> foundUserByName = findUserInUsers(newUserName, allUsers);
		assertThat(foundUserByName).isPresent();

		assertThat(systemUnderTest.validLogin(newUserName, newPassword).getName()).isEqualTo(newUserName);

		try {

			systemUnderTest.add(newUserName, newPassword);
		} catch (UserIsAlreadyKnown e) {
			assertThat(e).isNotNull();
		}

		systemUnderTest.remove(newUserName, newPassword);

		assertThat(systemUnderTest.validLogin(newUserName, newPassword)).isNull();
		systemUnderTest.removeFromFile(foundUserByName.get());

	}

	private Optional<User> findUserInUsers(String newUserName, Users allUsers) {
		return allUsers.getUsers().stream().filter(user -> user.getName().equals(newUserName)).findAny();
	}

	private Users readFromFile() throws IOException {
		byte[] file = Files.readAllBytes(Paths.get(systemUnderTest.FILE_NAME));
		String fileAsJSON = new String(file, StandardCharsets.UTF_8);
		Users allUsers = gson.fromJson(fileAsJSON, Users.class);
		return allUsers;
	}

	@Test
	public void canHashPasswords() {
		String myFancyPassword = "verySecret";
		byte[] hashed = systemUnderTest.hash(myFancyPassword);
		byte[] hashedAgain = systemUnderTest.hash("verySecret");
		assertThat(hashed).isEqualTo(hashedAgain);
		assertThat(hashed).isNotEqualTo(systemUnderTest.hash("SomethingDifferent"));
	}

}
