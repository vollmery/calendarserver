package de.jayaware.calendarserver.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import de.jayaware.calendarserver.model.User;
import lombok.Builder;
import lombok.Value;

/**
 * Mit dieser Klasse sollen die Sesion Tokens eingebaut.
 * 
 * @author BVO
 *
 */
public class SessionManagement {

	@Value
	@Builder
	static class Session {
		private long created;
		private User user;
	}

	private final Integer timeout;

	private final Map<String, Session> tokens = new HashMap<>();

	private Predicate<Session> isSessionExpired = null;

	private SessionManagement(Integer expiresIn) {
		this.timeout = expiresIn;
		isSessionExpired = session -> {
			long werta = Long.valueOf(session.getCreated() + timeout);
			long wertb = System.currentTimeMillis();
			return werta <= wertb;
		};
	}

	public String createTokenFor(User user) {
		Optional<Session> foundSession = tokens.values().stream()
				.filter(session -> session.getUser().getId().equals(user.getId())).findAny();

		if (foundSession.isPresent()) {
			for (Entry<String, Session> entry : tokens.entrySet()) {
				if (entry.getValue().equals(foundSession.get())) {
					if (!isSessionExpired.test(foundSession.get())) {
						return entry.getKey();
					} else {
						tokens.remove(entry.getKey());
						return null;
					}
				}
			}

		}
		String result = createToken();
		tokens.put(result, Session.builder().created(System.currentTimeMillis()).user(user).build());
		return result;

	}

	private String createToken() {
		return UUID.randomUUID().toString();
	}

	public boolean valid(String token) {
		Session session = tokens.get(token);
		if (session == null) {
			return false;
		}
		if (isSessionExpired.test(session)) {
			tokens.remove(token);
			return false;
		}
		return true;
	}

	public User getUser(String token) {
		return valid(token) ? tokens.get(token).getUser() : null;
	}

	public static SessionManagement expiresIn(int expiresInMillis) {
		return new SessionManagement(Integer.valueOf(expiresInMillis));
	}
}
