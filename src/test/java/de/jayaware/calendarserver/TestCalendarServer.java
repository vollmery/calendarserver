package de.jayaware.calendarserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import de.jayaware.calendarserver.exceptions.UserIsAlreadyKnown;
import de.jayaware.calendarserver.model.Login;
import de.jayaware.calendarserver.model.LoginResult;
import de.jayaware.calendarserver.model.User;
import de.jayaware.calendarserver.utils.SessionManagement;
import de.jayaware.calendarserver.utils.UserManagement;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import spark.Spark;

public class TestCalendarServer {

	private static final int EXPIRATION_TIME = 300;
	private static final int PORT = 8765;
	private static OkHttpClient client = new OkHttpClient.Builder().connectTimeout(300, TimeUnit.SECONDS).build();
	private final Gson gson = new Gson();

	private static UserManagement userManagement = UserManagement.getInstance();
	private static String userName = "fred feuerstein";
	private static String userPassword = "meinPasswort";
	private static User user;

	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private String baseURL = "http://localhost:" + PORT;;

	@BeforeClass
	public static void startServer() throws UserIsAlreadyKnown {
		userManagement.add(userName, userPassword);
		user = userManagement.validLogin(userName, userPassword);
		CalendarServer server = new CalendarServer(SessionManagement.expiresIn(EXPIRATION_TIME));
		server.start(PORT);
		Spark.awaitInitialization();
	}

	@Test
	public void canLogin() throws Exception {
		Login login = Login.builder().username(userName).password(userPassword).build();
		Response result = postToServer("/login", gson.toJson(login));
		LoginResult loginResult = gson.fromJson(result.body().string(), LoginResult.class);
		assertThat(loginResult.getToken()).isNotEmpty();

		Login negative = Login.builder().username("huiBuh").password("user").build();
		Response response = postToServer("/login", gson.toJson(negative));
		System.err.println(response.body().string());
	}

	private Response postToServer(String url, String payload) throws IOException {
		RequestBody body = RequestBody.create(JSON, payload);
		Request request = new Request.Builder().url(baseURL + url).post(body).build();
		return client.newCall(request).execute();
	}

	private String get(String url) throws Exception {
		Request request = new Request.Builder().url(baseURL + url).build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	@AfterClass
	public static void stopSpark() {
		userManagement.removeFromFile(user);
		Spark.stop();
	}

}
