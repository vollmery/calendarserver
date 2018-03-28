package de.jayaware.calendarserver.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.jayaware.calendarserver.exceptions.UserIsAlreadyKnown;
import de.jayaware.calendarserver.model.User;
import de.jayaware.calendarserver.model.Users;

/**
 * Diese Klasse ermöglicht Erstellung, Lesen und Veränderunng von BEnutzern.
 * 
 * @author BVO
 *
 */
public class UserManagement {

	private static Logger LOG = LoggerFactory.getLogger(UserManagement.class);

	public static final String FILE_NAME = "users.json";

	private static UserManagement INSTANCE = new UserManagement();
	// TODO: Auslagern in Systemumgebung bei server installation
	private String myLeakySalt = "Very LEaky Salt";

	private final Gson gson = new Gson();

	private Users users;

	private UserManagement() {
		users = readUsers();
	}

	public void add(String newUserName, String password) throws UserIsAlreadyKnown {

		User existing = validLogin(newUserName, password);
		if (existing == null) {
			User user = new User(UUID.randomUUID().toString());
			user.setName(newUserName);
			user.setPassword(hash(password));
			users.getUsers().add(user);
			writeUsers();
		} else {
			throw new UserIsAlreadyKnown();
		}

	}

	public User validLogin(String username, String password) {
		Optional<User> foundUser = users.getUsers().stream().filter(user -> {
			return user.getName().equals(username) && Arrays.equals(hash(password), user.getPassword())
					&& user.isActive();
		}).findAny();

		return foundUser.isPresent() ? foundUser.get() : null;
	}

	private void writeUsers() {
		String json = gson.toJson(users);
		try {
			Files.write(Paths.get(FILE_NAME), json.getBytes(StandardCharsets.UTF_8));
			users = readUsers();
		} catch (IOException e) {
			LOG.error("can not write to " + FILE_NAME);
		}
	}

	private Users readUsers() {
		try {
			byte[] fromFile = Files.readAllBytes(Paths.get(FILE_NAME));
			return gson.fromJson(new String(fromFile, StandardCharsets.UTF_8), Users.class);
		} catch (IOException e) {
			LOG.error("can not read " + FILE_NAME);
			return new Users();
		}
	}

	byte[] hash(String myFancyPassword) {
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(myFancyPassword.toCharArray(),
					myLeakySalt.getBytes(StandardCharsets.UTF_8), 666, 256);
			SecretKey key = skf.generateSecret(spec);
			byte[] res = key.getEncoded();
			return res;

		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}

	public static UserManagement getInstance() {
		return INSTANCE;
	}

	public void removeFromFile(User toRemove) {
		Predicate<User> isSameId = user -> user.getId().equals(toRemove.getId());
		users.getUsers().stream().filter(isSameId).findAny().ifPresent(user -> users.getUsers().remove(user));
		writeUsers();
	}

	public void remove(String nameToRemove, String passToRemove) {
		User existing = validLogin(nameToRemove, passToRemove);
		existing.setActive(Boolean.FALSE);
		writeUsers();
	}

}
